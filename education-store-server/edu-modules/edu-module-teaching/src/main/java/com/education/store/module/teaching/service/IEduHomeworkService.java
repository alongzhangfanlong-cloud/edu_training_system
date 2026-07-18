package com.education.store.module.teaching.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.teaching.entity.EduHomework;

import java.util.List;
import java.util.Map;

public interface IEduHomeworkService extends IService<EduHomework> {

    IPage<EduHomework> selectHomeworkList(IPage<EduHomework> page, String keyword, Long classId, Long teacherId, String startDate, String endDate);

    EduHomework addHomework(EduHomework homework, List<Long> attachmentIds);

    boolean updateHomework(EduHomework homework, List<Long> attachmentIds);

    boolean deleteHomework(Long id, Long currentUserId);

    Map<String, Object> getDetail(Long id);
}
