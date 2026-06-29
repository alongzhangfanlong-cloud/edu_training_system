package com.edu.modules.order.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.core.enums.*;
import com.edu.common.core.exception.BusinessException;
import com.edu.common.core.exception.ErrorCode;
import com.edu.common.core.utils.RedisDistributedLock;
import com.edu.common.mybatis.PageResult;
import com.edu.modules.consume.entity.HourTransactionEntity;
import com.edu.modules.consume.entity.StudentClassEntity;
import com.edu.modules.consume.mapper.HourTransactionMapper;
import com.edu.modules.consume.mapper.StudentClassMapper;
import com.edu.modules.course.entity.ClassEntity;
import com.edu.modules.course.entity.CourseEntity;
import com.edu.modules.course.mapper.ClassMapper;
import com.edu.modules.course.mapper.CourseMapper;
import com.edu.modules.order.dto.CreateOrderRequest;
import com.edu.modules.order.entity.OrderEntity;
import com.edu.modules.order.entity.OrderItemEntity;
import com.edu.modules.order.mapper.OrderItemMapper;
import com.edu.modules.order.mapper.OrderMapper;
import com.edu.modules.order.payment.PaymentService;
import com.edu.modules.student.mapper.StudentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final StudentMapper studentMapper;
    private final ClassMapper classMapper;
    private final CourseMapper courseMapper;
    private final StudentClassMapper studentClassMapper;
    private final HourTransactionMapper hourTransactionMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private final PaymentService paymentService;
    private final RedisDistributedLock distributedLock;

    private static final String ORDER_SEQ_KEY = "order:seq";

    /**
     * 创建报名订单 — 仅创建订单，不涉及支付和课时
     */
    @Transactional
    public Map<String, Object> createOrder(CreateOrderRequest request) {
        // 1. 校验学员
        if (studentMapper.selectById(request.getStudentId()) == null) {
            throw new BusinessException(ErrorCode.STUDENT_NOT_FOUND);
        }

        // 2. 校验并计算
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CreateOrderRequest.OrderItem item : request.getItems()) {
            // 校验课程存在
            CourseEntity course = courseMapper.selectById(item.getCourseId());
            if (course == null) throw new BusinessException(ErrorCode.COURSE_NOT_FOUND, "课程ID=" + item.getCourseId());

            // 校验班级存在且未满
            if (item.getClassId() != null) {
                ClassEntity cls = classMapper.selectById(item.getClassId());
                if (cls == null) throw new BusinessException(ErrorCode.CLASS_NOT_FOUND, "班级ID=" + item.getClassId());
                if (cls.getCurrentStudents() >= cls.getMaxStudents()) {
                    throw new BusinessException(ErrorCode.CLASS_FULL,
                            String.format("班级「%s」已满员(%d/%d)", cls.getName(), cls.getCurrentStudents(), cls.getMaxStudents()));
                }
                // 防重复报名
                LambdaQueryWrapper<StudentClassEntity> dupCheck = new LambdaQueryWrapper<>();
                dupCheck.eq(StudentClassEntity::getStudentId, request.getStudentId())
                        .eq(StudentClassEntity::getClassId, item.getClassId())
                        .eq(StudentClassEntity::getStatus, 1);
                if (studentClassMapper.selectCount(dupCheck) > 0) {
                    throw new BusinessException(ErrorCode.DUPLICATE_ENROLLMENT,
                            "学员已报名该班级，请勿重复报名");
                }
            }

            BigDecimal price = item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO;
            BigDecimal hours = item.getHours() != null ? item.getHours() : BigDecimal.ZERO;
            totalAmount = totalAmount.add(price.multiply(hours));
        }

        // 3. 创建订单
        String orderNo = generateOrderNo();
        OrderEntity order = new OrderEntity();
        order.setOrderNo(orderNo);
        order.setStudentId(request.getStudentId());
        order.setType(request.getType() != null ? request.getType() : OrderTypeEnum.NEW.getCode());
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setPayAmount(totalAmount);
        order.setPayStatus(PayStatusEnum.UNPAID.getCode());
        order.setRemark(request.getRemark());
        orderMapper.insert(order);

        // 4. 创建订单明细
        for (CreateOrderRequest.OrderItem item : request.getItems()) {
            OrderItemEntity oi = new OrderItemEntity();
            oi.setOrderId(order.getId());
            oi.setCourseId(item.getCourseId());
            oi.setClassId(item.getClassId());
            oi.setItemType(item.getItemType() != null ? item.getItemType() : 1);
            oi.setHours(item.getHours());
            oi.setUnitPrice(item.getUnitPrice());
            oi.setTotalPrice(item.getUnitPrice() != null && item.getHours() != null
                    ? item.getUnitPrice().multiply(item.getHours()) : BigDecimal.ZERO);
            oi.setGiftHours(item.getGiftHours() != null ? item.getGiftHours() : BigDecimal.ZERO);
            orderItemMapper.insert(oi);
        }

        log.info("订单创建成功: orderNo={}, studentId={}, amount={}", orderNo, request.getStudentId(), totalAmount);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderId", order.getId());
        result.put("orderNo", orderNo);
        result.put("totalAmount", totalAmount);
        result.put("discountAmount", BigDecimal.ZERO);
        result.put("payAmount", totalAmount);
        return result;
    }

    /**
     * 订单支付 — 核心事务
     *
     * 并发保护:
     * 1. 分布式锁 lock:enrollment:class:{classId} 防止同班级并发报名超卖
     * 2. 幂等: 已支付订单直接返回
     * 3. SELECT FOR UPDATE 行锁防并发扣课时
     * 4. 班级容量二次校验(锁内)
     * 5. 防重复报名校验
     */
    @Transactional
    public Map<String, Object> pay(Long orderId, Integer payMethod, String offlineRemark) {
        // 1. 查询订单
        OrderEntity order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);

        // 2. 幂等: 已支付直接返回
        if (Objects.equals(order.getPayStatus(), PayStatusEnum.PAID.getCode())) {
            log.info("订单已支付(幂等): orderNo={}", order.getOrderNo());
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("payStatus", PayStatusEnum.PAID.getCode());
            result.put("payStatusName", "已支付");
            result.put("message", "订单已支付，无需重复操作");
            return result;
        }

        // 3. 获取订单明细
        List<OrderItemEntity> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItemEntity>().eq(OrderItemEntity::getOrderId, orderId));

        // 4. 预检: 所有班级可报名 (支付前检查，避免支付后报名失败)
        for (OrderItemEntity item : items) {
            if (item.getClassId() == null) continue;
            // 防重复
            LambdaQueryWrapper<StudentClassEntity> dupCheck = new LambdaQueryWrapper<>();
            dupCheck.eq(StudentClassEntity::getStudentId, order.getStudentId())
                    .eq(StudentClassEntity::getClassId, item.getClassId())
                    .eq(StudentClassEntity::getStatus, 1);
            if (studentClassMapper.selectCount(dupCheck) > 0) {
                throw new BusinessException(ErrorCode.DUPLICATE_ENROLLMENT,
                        "学员已报名该班级，请取消此订单");
            }
            // 容量检查
            ClassEntity cls = classMapper.selectById(item.getClassId());
            if (cls != null && cls.getCurrentStudents() >= cls.getMaxStudents()) {
                throw new BusinessException(ErrorCode.CLASS_FULL,
                        String.format("班级「%s」已满员(%d/%d)", cls.getName(), cls.getCurrentStudents(), cls.getMaxStudents()));
            }
        }

        // 5. 逐条处理报名 (分布式锁保护)
        for (OrderItemEntity item : items) {
            if (item.getClassId() == null) continue;

            String lockKey = RedisDistributedLock.enrollmentLock(item.getClassId());
            distributedLock.executeWithLock(lockKey, () -> {
                enrollStudentToClass(order.getStudentId(), item, orderId);
                return null;
            });
        }

        // 6. 最后更新支付状态 (报名成功后才标记已支付)
        order.setPayStatus(PayStatusEnum.PAID.getCode());
        order.setPayMethod(payMethod != null ? payMethod : PayMethodEnum.OFFLINE.getCode());
        order.setPayTime(LocalDateTime.now());
        if (payMethod != null && payMethod == PayMethodEnum.OFFLINE.getCode() && offlineRemark != null) {
            order.setRemark((order.getRemark() != null ? order.getRemark() + "; " : "") + offlineRemark);
        }
        orderMapper.updateById(order);

        log.info("订单支付完成: orderNo={}, payMethod={}", order.getOrderNo(),
                payMethod != null && payMethod == 1 ? "微信" : payMethod == 2 ? "支付宝" : "线下");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("payStatus", PayStatusEnum.PAID.getCode());
        result.put("payStatusName", "已支付");
        result.put("message", "支付成功");

        // 如果是微信/支付宝支付，返回支付参数（开发模式返回模拟二维码）
        if (payMethod != null && (payMethod == 1 || payMethod == 2)) {
            String amount = order.getPayAmount() != null ? order.getPayAmount().toString() : "0";
            Map<String, Object> payParams = paymentService.createPayment(
                    order.getOrderNo(), amount, payMethod);
            result.put("payment", payParams);
        }
        return result;
    }

    /**
     * 学员报名到班级 — 锁内执行
     */
    private void enrollStudentToClass(Long studentId, OrderItemEntity item, Long orderId) {
        // 二次防重复检查 (锁内)
        LambdaQueryWrapper<StudentClassEntity> dupCheck = new LambdaQueryWrapper<>();
        dupCheck.eq(StudentClassEntity::getStudentId, studentId)
                .eq(StudentClassEntity::getClassId, item.getClassId())
                .eq(StudentClassEntity::getStatus, 1);
        if (studentClassMapper.selectCount(dupCheck) > 0) {
            throw new BusinessException(ErrorCode.DUPLICATE_ENROLLMENT,
                    "学员已报名该班级，支付已取消。请手动取消订单");
        }

        // 班级容量检查 (锁内)
        ClassEntity cls = classMapper.selectById(item.getClassId());
        if (cls == null) throw new BusinessException(ErrorCode.CLASS_NOT_FOUND);
        if (cls.getCurrentStudents() >= cls.getMaxStudents()) {
            throw new BusinessException(ErrorCode.CLASS_FULL,
                    String.format("班级「%s」已满员(%d/%d)", cls.getName(), cls.getCurrentStudents(), cls.getMaxStudents()));
        }

        BigDecimal totalHours = item.getHours() != null ? item.getHours() : BigDecimal.ZERO;
        BigDecimal giftHours = item.getGiftHours() != null ? item.getGiftHours() : BigDecimal.ZERO;
        BigDecimal addHours = totalHours.add(giftHours);

        // 创建/更新学员班级关系 (SELECT FOR UPDATE)
        StudentClassEntity sc = studentClassMapper.selectForUpdate(studentId, item.getClassId());
        BigDecimal beforeHours = (sc != null) ? sc.getRemainHours() : BigDecimal.ZERO;

        if (sc == null) {
            sc = new StudentClassEntity();
            sc.setStudentId(studentId);
            sc.setClassId(item.getClassId());
            sc.setOrderId(orderId);
            sc.setTotalHours(addHours);
            sc.setUsedHours(BigDecimal.ZERO);
            sc.setRemainHours(addHours);
            sc.setGiftHours(giftHours);
            sc.setStatus(1);
            studentClassMapper.insert(sc);
        } else {
            // 续费/扩科场景: 累加课时
            sc.setTotalHours(sc.getTotalHours().add(addHours));
            sc.setRemainHours(sc.getRemainHours().add(addHours));
            sc.setGiftHours(sc.getGiftHours().add(giftHours));
            sc.setStatus(1); // 如有停课则恢复
            studentClassMapper.updateById(sc);
        }

        // 课时流水 — 报名增加
        HourTransactionEntity tx = new HourTransactionEntity();
        tx.setStudentId(studentId);
        tx.setStudentClassId(sc.getId());
        tx.setType(HourTransactionTypeEnum.ENROLLMENT.getCode());
        tx.setChangeHours(addHours);
        tx.setBeforeHours(beforeHours);
        tx.setAfterHours(sc.getRemainHours());
        tx.setOrderId(orderId);
        tx.setRemark(String.format("报名增加%.1f课时%s", totalHours,
                giftHours.compareTo(BigDecimal.ZERO) > 0 ? "，赠送" + giftHours + "课时" : ""));
        hourTransactionMapper.insert(tx);

        // 赠课单独流水
        if (giftHours.compareTo(BigDecimal.ZERO) > 0) {
            HourTransactionEntity giftTx = new HourTransactionEntity();
            giftTx.setStudentId(studentId);
            giftTx.setStudentClassId(sc.getId());
            giftTx.setType(HourTransactionTypeEnum.GIFT.getCode());
            giftTx.setChangeHours(giftHours);
            giftTx.setBeforeHours(beforeHours.add(totalHours));
            giftTx.setAfterHours(sc.getRemainHours());
            giftTx.setOrderId(orderId);
            giftTx.setRemark("赠送课时");
            hourTransactionMapper.insert(giftTx);
        }

        // 更新班级人数
        cls.setCurrentStudents(cls.getCurrentStudents() + 1);
        classMapper.updateById(cls);

        log.info("学员报名成功: studentId={}, classId={}, hours={}, gift={}, remain={}",
                studentId, item.getClassId(), totalHours, giftHours, sc.getRemainHours());
    }

    /**
     * 订单详情
     */
    public Map<String, Object> getDetail(Long orderId) {
        OrderEntity order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);

        List<OrderItemEntity> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItemEntity>().eq(OrderItemEntity::getOrderId, orderId));

        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("id", order.getId());
        detail.put("orderNo", order.getOrderNo());
        detail.put("studentId", order.getStudentId());
        detail.put("type", order.getType());
        detail.put("typeName", order.getType() != null ? OrderTypeEnum.NEW.getDesc() : "");
        detail.put("totalAmount", order.getTotalAmount());
        detail.put("discountAmount", order.getDiscountAmount());
        detail.put("payAmount", order.getPayAmount());
        detail.put("payStatus", order.getPayStatus());
        detail.put("payStatusName", PayStatusEnum.PAID.getCode() == (order.getPayStatus() != null ? order.getPayStatus() : 0) ? "已支付" : "未支付");
        detail.put("payMethod", order.getPayMethod());
        detail.put("payMethodName", order.getPayMethod() != null ? PayMethodEnum.OFFLINE.getDesc() : "");
        detail.put("payTime", order.getPayTime());
        detail.put("items", items);
        detail.put("createdAt", order.getCreatedAt());
        return detail;
    }

    /**
     * 订单列表 — 分页
     */
    public PageResult<Map<String, Object>> list(Long studentId, Integer payStatus, Integer page, Integer pageSize) {
        LambdaQueryWrapper<OrderEntity> wrapper = new LambdaQueryWrapper<>();
        if (studentId != null) wrapper.eq(OrderEntity::getStudentId, studentId);
        if (payStatus != null) wrapper.eq(OrderEntity::getPayStatus, payStatus);
        wrapper.orderByDesc(OrderEntity::getCreatedAt);

        Page<OrderEntity> p = new Page<>(page, Math.min(pageSize, 100));
        Page<OrderEntity> result = orderMapper.selectPage(p, wrapper);

        List<Map<String, Object>> list = new ArrayList<>();
        for (OrderEntity o : result.getRecords()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", o.getId()); m.put("orderNo", o.getOrderNo());
            m.put("studentId", o.getStudentId()); m.put("type", o.getType());
            m.put("totalAmount", o.getTotalAmount()); m.put("payAmount", o.getPayAmount());
            m.put("payStatus", o.getPayStatus()); m.put("payStatusName", o.getPayStatus() == 1 ? "已支付" : "未支付");
            m.put("payMethod", o.getPayMethod()); m.put("createdAt", o.getCreatedAt());
            list.add(m);
        }

        return PageResult.of(list, result.getTotal(), page, pageSize);
    }

    /**
     * 财务摘要 - 今日/本月营收统计
     */
    public Map<String, Object> financeSummary() {
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);

        LambdaQueryWrapper<OrderEntity> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.eq(OrderEntity::getPayStatus, 1)
                    .ge(OrderEntity::getPayTime, today.atStartOfDay())
                    .le(OrderEntity::getPayTime, today.plusDays(1).atStartOfDay());
        List<OrderEntity> todayOrders = orderMapper.selectList(todayWrapper);

        LambdaQueryWrapper<OrderEntity> monthWrapper = new LambdaQueryWrapper<>();
        monthWrapper.eq(OrderEntity::getPayStatus, 1)
                     .ge(OrderEntity::getPayTime, monthStart.atStartOfDay());
        List<OrderEntity> monthOrders = orderMapper.selectList(monthWrapper);

        BigDecimal todayRevenue = todayOrders.stream()
                .map(o -> o.getPayAmount() != null ? o.getPayAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal monthRevenue = monthOrders.stream()
                .map(o -> o.getPayAmount() != null ? o.getPayAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 待支付订单
        LambdaQueryWrapper<OrderEntity> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(OrderEntity::getPayStatus, 0);
        long pendingCount = orderMapper.selectCount(pendingWrapper);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("todayRevenue", todayRevenue);
        result.put("monthRevenue", monthRevenue);
        result.put("todayOrderCount", todayOrders.size());
        result.put("monthOrderCount", monthOrders.size());
        result.put("pendingCount", pendingCount);
        return result;
    }

    private String generateOrderNo() {
        String year = String.valueOf(Year.now().getValue());
        Long seq = redisTemplate.opsForValue().increment(ORDER_SEQ_KEY + ":" + year);
        return "ORD" + year + String.format("%08d", seq);
    }
}
