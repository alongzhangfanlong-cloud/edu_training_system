package com.education.store.module.tuition.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.utils.SecurityUtils;
import com.education.store.module.student.entity.EduStudentHoursLog;
import com.education.store.module.student.mapper.EduStudentHoursLogMapper;
import com.education.store.module.student.mapper.EduStudentMapper;
import com.education.store.module.tuition.entity.EduTuitionGiftHours;
import com.education.store.module.tuition.mapper.EduTuitionGiftHoursMapper;
import com.education.store.module.tuition.service.IEduTuitionGiftHoursService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class EduTuitionGiftHoursServiceImpl extends ServiceImpl<EduTuitionGiftHoursMapper, EduTuitionGiftHours>
        implements IEduTuitionGiftHoursService {

    private final EduTuitionGiftHoursMapper giftMapper;
    private final EduStudentHoursLogMapper hoursLogMapper;
    private final EduStudentMapper studentMapper;

    @Override
    public IPage<EduTuitionGiftHours> selectGiftList(IPage<EduTuitionGiftHours> page, EduTuitionGiftHours gift) {
        LambdaQueryWrapper<EduTuitionGiftHours> wrapper = new LambdaQueryWrapper<>();
        if (gift.getStudentId() != null) wrapper.eq(EduTuitionGiftHours::getStudentId, gift.getStudentId());
        wrapper.orderByDesc(EduTuitionGiftHours::getCreateTime);
        return giftMapper.selectPage(page, wrapper);
    }

    @Override public EduTuitionGiftHours selectGiftById(Long id) { return giftMapper.selectById(id); }

    @Override @Transactional(rollbackFor = Exception.class)
    public int insertGift(EduTuitionGiftHours gift) {
        gift.setGiftNo(generateGiftNo());
        gift.setCreateBy(SecurityUtils.getUserIdAsString());
        int rows = giftMapper.insert(gift);

        // 写课时日志
        BigDecimal gainAmount = new BigDecimal(gift.getHours());
        EduStudentHoursLog log = new EduStudentHoursLog();
        log.setStudentId(gift.getStudentId());
        log.setCourseId(gift.getCourseId());
        log.setChangeType("GIFT_GAIN");
        log.setChangeAmount(gainAmount);
        log.setSourceType("GIFT");
        log.setSourceId(gift.getId());
        log.setRemark(gift.getGiftNo());
        BigDecimal current = hoursLogMapper.sumBalanceByStudent(gift.getStudentId());
        if (current == null) current = BigDecimal.ZERO;
        log.setBalanceAfter(current.add(gainAmount));
        hoursLogMapper.insert(log);

        var total = hoursLogMapper.sumTotalGainedByStudent(gift.getStudentId());
        var remaining = hoursLogMapper.sumBalanceByStudent(gift.getStudentId());
        var student = studentMapper.selectById(gift.getStudentId());
        if (student != null) {
            student.setTotalHours(total != null ? total : BigDecimal.ZERO);
            student.setRemainingHours(remaining != null ? remaining : BigDecimal.ZERO);
            studentMapper.updateById(student);
        }
        return rows;
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int deleteGiftByIds(Long[] ids) { return giftMapper.deleteBatchIds(Arrays.asList(ids)); }

    private String generateGiftNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
        String random = String.format("%02d", ThreadLocalRandom.current().nextInt(100));
        return "G" + dateStr + timeStr + random;
    }
}
