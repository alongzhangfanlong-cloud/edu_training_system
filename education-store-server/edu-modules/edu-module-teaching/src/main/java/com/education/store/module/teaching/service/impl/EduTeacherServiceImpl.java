package com.education.store.module.teaching.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.module.teaching.entity.EduTeacher;
import com.education.store.module.teaching.mapper.EduTeacherMapper;
import com.education.store.module.teaching.service.IEduTeacherService;
import org.springframework.stereotype.Service;

/**
 * 教师档案 Service 实现
 */
@Service
public class EduTeacherServiceImpl extends ServiceImpl<EduTeacherMapper, EduTeacher>
        implements IEduTeacherService {
}
