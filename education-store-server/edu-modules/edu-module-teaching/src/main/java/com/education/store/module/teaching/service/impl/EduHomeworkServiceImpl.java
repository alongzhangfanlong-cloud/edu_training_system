package com.education.store.module.teaching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.classinfo.entity.EduClass;
import com.education.store.module.classinfo.mapper.EduClassMapper;
import com.education.store.module.course.entity.EduCourse;
import com.education.store.module.course.mapper.EduCourseMapper;
import com.education.store.module.schedule.entity.EduSchedule;
import com.education.store.module.schedule.mapper.EduScheduleMapper;
import com.education.store.module.system.entity.system.SysUser;
import com.education.store.module.system.mapper.SysUserMapper;
import com.education.store.module.teaching.entity.EduHomework;
import com.education.store.module.teaching.entity.EduHomeworkAttachment;
import com.education.store.module.teaching.entity.EduHomeworkSubmission;
import com.education.store.module.teaching.mapper.EduHomeworkAttachmentMapper;
import com.education.store.module.teaching.mapper.EduHomeworkMapper;
import com.education.store.module.teaching.mapper.EduHomeworkSubmissionMapper;
import com.education.store.module.teaching.service.IEduHomeworkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import com.education.store.common.event.HomeworkPublishedEvent;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EduHomeworkServiceImpl extends ServiceImpl<EduHomeworkMapper, EduHomework>
        implements IEduHomeworkService {

    private final EduHomeworkMapper homeworkMapper;
    private final EduHomeworkAttachmentMapper attachmentMapper;
    private final EduHomeworkSubmissionMapper submissionMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final EduClassMapper classMapper;
    private final EduCourseMapper courseMapper;
    private final SysUserMapper sysUserMapper;
    private final EduScheduleMapper scheduleMapper;

    @Override
    public IPage<EduHomework> selectHomeworkList(IPage<EduHomework> page, String keyword, Long classId, Long teacherId, String startDate, String endDate) {
        LambdaQueryWrapper<EduHomework> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(EduHomework::getTitle, keyword);
        }
        if (classId != null) {
            wrapper.eq(EduHomework::getClassId, classId);
        }
        if (teacherId != null) {
            wrapper.eq(EduHomework::getTeacherId, teacherId);
        }
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(EduHomework::getCreateTime, startDate + " 00:00:00");
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(EduHomework::getCreateTime, endDate + " 23:59:59");
        }
        wrapper.orderByDesc(EduHomework::getCreateTime);
        IPage<EduHomework> result = homeworkMapper.selectPage(page, wrapper);
        fillNamesForList(result.getRecords());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EduHomework addHomework(EduHomework homework, List<Long> attachmentIds) {
        homeworkMapper.insert(homework);
        if (attachmentIds != null && !attachmentIds.isEmpty()) {
            linkAttachments(homework.getId(), attachmentIds);
        }
        // 发布作业通知事件
        eventPublisher.publishEvent(new HomeworkPublishedEvent(
            this, homework.getId(), homework.getClassId(),
            "",  // className — 从班级表查询，无法获取则留空
            homework.getTitle(), homework.getDeadline()));
        return homework;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateHomework(EduHomework homework, List<Long> attachmentIds) {
        EduHomework existing = homeworkMapper.selectById(homework.getId());
        if (existing == null) {
            throw new BusinessException("404", "作业不存在");
        }
        homework.setIsUpdated(1);
        if (attachmentIds != null && !attachmentIds.isEmpty()) {
            linkAttachments(homework.getId(), attachmentIds);
        }
        return updateById(homework);
    }

    @Override
    public Map<String, Object> getDetail(Long id) {
        EduHomework homework = homeworkMapper.selectById(id);
        if (homework == null) return Collections.emptyMap();

        Map<String, Object> result = new HashMap<>();
        result.put("id", homework.getId());
        result.put("classId", homework.getClassId());
        result.put("courseId", homework.getCourseId());
        result.put("scheduleId", homework.getScheduleId());
        result.put("title", homework.getTitle());
        result.put("content", homework.getContent());
        result.put("teacherId", homework.getTeacherId());
        result.put("isUpdated", homework.getIsUpdated());
        result.put("createTime", homework.getCreateTime());
        result.put("updateTime", homework.getUpdateTime());

        // 附件
        List<EduHomeworkAttachment> attachments = attachmentMapper.selectByHomeworkId(id);
        result.put("attachments", attachments);

        // 提交记录
        LambdaQueryWrapper<EduHomeworkSubmission> subWrapper = new LambdaQueryWrapper<>();
        subWrapper.eq(EduHomeworkSubmission::getHomeworkId, id);
        subWrapper.orderByDesc(EduHomeworkSubmission::getCreateTime);
        List<EduHomeworkSubmission> submissions = submissionMapper.selectList(subWrapper);
        result.put("submissions", submissions);

        return result;
    }

    private void linkAttachments(Long homeworkId, List<Long> attachmentIds) {
        // 删除旧附件关联
        LambdaQueryWrapper<EduHomeworkAttachment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduHomeworkAttachment::getHomeworkId, homeworkId);
        attachmentMapper.delete(wrapper);
        // 重新绑定
        for (int i = 0; i < attachmentIds.size(); i++) {
            EduHomeworkAttachment attachment = new EduHomeworkAttachment();
            attachment.setHomeworkId(homeworkId);
            attachment.setFileUrl(String.valueOf(attachmentIds.get(i)));
            attachment.setSortOrder(i);
            attachmentMapper.insert(attachment);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteHomework(Long id, Long currentUserId) {
        EduHomework existing = homeworkMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("404", "作业不存在");
        }
        return removeById(id);
    }

    private void fillNamesForList(List<EduHomework> list) {
        if (list == null || list.isEmpty()) return;
        Set<Long> classIds = new HashSet<>();
        Set<Long> courseIds = new HashSet<>();
        Set<Long> teacherIds = new HashSet<>();
        Set<Long> scheduleIds = new HashSet<>();
        for (EduHomework homework : list) {
            if (homework.getClassId() != null) classIds.add(homework.getClassId());
            if (homework.getCourseId() != null) courseIds.add(homework.getCourseId());
            if (homework.getTeacherId() != null) teacherIds.add(homework.getTeacherId());
            if (homework.getScheduleId() != null) scheduleIds.add(homework.getScheduleId());
        }
        Map<Long, EduClass> classMap = classIds.isEmpty() ? Collections.emptyMap()
                : classMapper.selectBatchIds(classIds).stream().collect(Collectors.toMap(EduClass::getId, c -> c));
        Map<Long, EduCourse> courseMap = courseIds.isEmpty() ? Collections.emptyMap()
                : courseMapper.selectBatchIds(courseIds).stream().collect(Collectors.toMap(EduCourse::getId, c -> c));
        Map<Long, SysUser> userMap = teacherIds.isEmpty() ? Collections.emptyMap()
                : sysUserMapper.selectBatchIds(teacherIds).stream().collect(Collectors.toMap(SysUser::getId, u -> u));
        Map<Long, EduSchedule> scheduleMap = scheduleIds.isEmpty() ? Collections.emptyMap()
                : scheduleMapper.selectBatchIds(scheduleIds).stream().collect(Collectors.toMap(EduSchedule::getId, s -> s));
        for (EduHomework homework : list) {
            if (homework.getClassId() != null) {
                EduClass clazz = classMap.get(homework.getClassId());
                if (clazz != null) {
                    homework.setClassName(clazz.getName());
                    if (homework.getCourseId() == null && clazz.getCourseId() != null) {
                        EduCourse course = courseMap.get(clazz.getCourseId());
                        if (course != null) homework.setCourseName(course.getName());
                    }
                }
            }
            if (homework.getCourseId() != null) {
                EduCourse course = courseMap.get(homework.getCourseId());
                if (course != null) homework.setCourseName(course.getName());
            }
            if (homework.getTeacherId() != null) {
                SysUser user = userMap.get(homework.getTeacherId());
                if (user != null) homework.setTeacherName(user.getRealName());
            }
            if (homework.getScheduleId() != null) {
                EduSchedule schedule = scheduleMap.get(homework.getScheduleId());
                if (schedule != null) homework.setScheduleDate(schedule.getScheduleDate());
            }
        }
    }
}
