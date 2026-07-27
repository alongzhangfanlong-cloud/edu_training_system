package com.education.store.module.student.controller;

import com.education.store.common.model.Result;
import com.education.store.common.utils.SecurityUtils;
import com.education.store.module.student.entity.EduStudent;
import com.education.store.module.student.entity.EduStudentParent;
import com.education.store.module.student.mapper.EduStudentMapper;
import com.education.store.module.student.mapper.EduStudentParentMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 家长端基础 Controller — 孩子列表和详情
 *
 * <p>扩展的跨模块家长端 API（考勤/作业/课表/积分等）见
 * edu-module-dashboard 中的 {@code ParentApiController}
 */
@Tag(name = "家长端-基础")
@RestController
@RequestMapping("/api/v1/parent")
@RequiredArgsConstructor
public class ParentController {

    private final EduStudentParentMapper parentMapper;
    private final EduStudentMapper studentMapper;

    @Operation(summary = "获取当前家长关联的所有孩子（含摘要信息）")
    @GetMapping("/children")
    public Result<List<Map<String, Object>>> getChildren() {
        Long userId = SecurityUtils.getUserId();

        List<EduStudent> students;
        
        // 管理员可以查看所有学生
        if (SecurityUtils.isAdmin()) {
            students = studentMapper.selectList(null);
        } else {
            List<EduStudentParent> relations = parentMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EduStudentParent>()
                            .eq(EduStudentParent::getUserId, userId));

            if (relations.isEmpty()) {
                return Result.success(Collections.emptyList());
            }

            List<Long> studentIds = relations.stream()
                    .map(EduStudentParent::getStudentId)
                    .distinct()
                    .collect(Collectors.toList());

            students = studentMapper.selectBatchIds(studentIds);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (EduStudent student : students) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("studentId", student.getId());
            item.put("name", student.getName());
            item.put("grade", student.getGrade());
            item.put("gender", student.getGender());
            item.put("school", student.getSchool());
            item.put("totalHours", student.getTotalHours());
            item.put("remainingHours", student.getRemainingHours());
            item.put("status", student.getStatus());
            result.add(item);
        }

        return Result.success(result);
    }

    @Operation(summary = "查看孩子详情")
    @GetMapping("/children/{studentId}")
    public Result<Map<String, Object>> getChildDetail(@PathVariable Long studentId) {
        Long userId = SecurityUtils.getUserId();
        // 管理员可以查看任何学生，家长只能看自己的孩子
        if (!SecurityUtils.isAdmin() && !isParentOf(userId, studentId)) {
            return Result.error("无权查看该学员信息");
        }

        EduStudent student = studentMapper.selectById(studentId);
        if (student == null) {
            return Result.error("学员不存在");
        }

        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("studentId", student.getId());
        detail.put("name", student.getName());
        detail.put("grade", student.getGrade());
        detail.put("gender", student.getGender());
        detail.put("school", student.getSchool());
        detail.put("mobile", student.getMobile());
        detail.put("totalHours", student.getTotalHours());
        detail.put("remainingHours", student.getRemainingHours());
        detail.put("status", student.getStatus());
        detail.put("remark", student.getRemark());

        return Result.success(detail);
    }

    /** 判断某个学生是否属于当前家长的关联孩子 */
    private boolean isParentOf(Long userId, Long studentId) {
        Long count = parentMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EduStudentParent>()
                        .eq(EduStudentParent::getUserId, userId)
                        .eq(EduStudentParent::getStudentId, studentId));
        return count != null && count > 0;
    }
}
