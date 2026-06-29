package com.edu.modules.feedback.service;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.edu.common.core.exception.BusinessException;
import com.edu.common.core.exception.ErrorCode;
import com.edu.modules.feedback.dto.FeedbackRequest;
import com.edu.modules.feedback.entity.ClassFeedbackEntity;
import com.edu.modules.feedback.entity.StudentFeedbackEntity;
import com.edu.modules.feedback.mapper.ClassFeedbackMapper;
import com.edu.modules.feedback.mapper.StudentFeedbackMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final ClassFeedbackMapper classFeedbackMapper;
    private final StudentFeedbackMapper studentFeedbackMapper;

    @Transactional
    public Long submit(FeedbackRequest request) {
        // 创建课堂反馈
        ClassFeedbackEntity classFeedback = new ClassFeedbackEntity();
        classFeedback.setScheduleId(request.getScheduleId());
        classFeedback.setContent(request.getContent());
        classFeedback.setHomework(request.getHomework());
        classFeedback.setPhotos(request.getPhotos() != null ? JSONUtil.toJsonStr(request.getPhotos()) : null);
        classFeedback.setOverallComment(request.getOverallComment());
        classFeedback.setNextPreview(request.getNextPreview());
        classFeedbackMapper.insert(classFeedback);

        // 创建学员个人反馈
        if (request.getStudentFeedbacks() != null) {
            for (FeedbackRequest.StudentFeedbackItem item : request.getStudentFeedbacks()) {
                StudentFeedbackEntity sf = new StudentFeedbackEntity();
                sf.setScheduleId(request.getScheduleId());
                sf.setStudentId(item.getStudentId());
                sf.setTeacherId(classFeedback.getTeacherId());
                sf.setPerformanceScore(item.getPerformanceScore());
                sf.setPerformanceComment(item.getPerformanceComment());
                sf.setHomeworkStatus(item.getHomeworkStatus());
                studentFeedbackMapper.insert(sf);
            }
        }

        log.info("提交课堂反馈成功: scheduleId={}, classFeedbackId={}",
                request.getScheduleId(), classFeedback.getId());
        return classFeedback.getId();
    }

    public List<Map<String, Object>> listAll() {
        LambdaQueryWrapper<ClassFeedbackEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ClassFeedbackEntity::getCreatedAt);
        List<ClassFeedbackEntity> feedbacks = classFeedbackMapper.selectList(wrapper);

        return feedbacks.stream().map(feedback -> {
            Map<String, Object> result = new HashMap<>();
            result.put("id", feedback.getId());
            result.put("scheduleId", feedback.getScheduleId());
            result.put("classId", feedback.getClassId());
            result.put("teacherId", feedback.getTeacherId());
            result.put("content", feedback.getContent());
            result.put("homework", feedback.getHomework());
            result.put("overallComment", feedback.getOverallComment());
            result.put("nextPreview", feedback.getNextPreview());
            result.put("createdAt", feedback.getCreatedAt());
            return result;
        }).collect(Collectors.toList());
    }

    public Map<String, Object> getByScheduleId(Long scheduleId) {
        LambdaQueryWrapper<ClassFeedbackEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassFeedbackEntity::getScheduleId, scheduleId)
                .orderByDesc(ClassFeedbackEntity::getCreatedAt)
                .last("LIMIT 1");
        ClassFeedbackEntity feedback = classFeedbackMapper.selectOne(wrapper);

        if (feedback == null) {
            return Collections.emptyMap();
        }

        // 获取学员反馈列表
        LambdaQueryWrapper<StudentFeedbackEntity> sfWrapper = new LambdaQueryWrapper<>();
        sfWrapper.eq(StudentFeedbackEntity::getScheduleId, scheduleId);
        List<StudentFeedbackEntity> studentFeedbacks = studentFeedbackMapper.selectList(sfWrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("id", feedback.getId());
        result.put("scheduleId", feedback.getScheduleId());
        result.put("content", feedback.getContent());
        result.put("homework", feedback.getHomework());
        result.put("photos", feedback.getPhotos() != null
                ? JSONUtil.parseArray(feedback.getPhotos()).toList(String.class) : Collections.emptyList());
        result.put("overallComment", feedback.getOverallComment());
        result.put("nextPreview", feedback.getNextPreview());
        result.put("studentFeedbacks", studentFeedbacks);
        result.put("createdAt", feedback.getCreatedAt());
        return result;
    }

    public void delete(Long id) {
        classFeedbackMapper.deleteById(id);
        log.info("删除课堂反馈: id={}", id);
    }

    public Map<String, Object> getStudentFeedback(Long scheduleId, Long studentId) {
        LambdaQueryWrapper<StudentFeedbackEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentFeedbackEntity::getScheduleId, scheduleId)
                .eq(StudentFeedbackEntity::getStudentId, studentId)
                .last("LIMIT 1");
        StudentFeedbackEntity sf = studentFeedbackMapper.selectOne(wrapper);

        if (sf == null) {
            return Collections.emptyMap();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", sf.getId());
        result.put("performanceScore", sf.getPerformanceScore());
        result.put("performanceComment", sf.getPerformanceComment());
        result.put("homeworkStatus", sf.getHomeworkStatus());
        result.put("createdAt", sf.getCreatedAt());
        return result;
    }
}
