package com.edu.modules.interaction.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.edu.modules.interaction.entity.BirthdayGreetingEntity;
import com.edu.modules.interaction.mapper.BirthdayGreetingMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BirthdayGreetingService {

    private final BirthdayGreetingMapper birthdayGreetingMapper;

    public List<BirthdayGreetingEntity> list(Long studentId) {
        LambdaQueryWrapper<BirthdayGreetingEntity> wrapper = new LambdaQueryWrapper<>();
        if (studentId != null) wrapper.eq(BirthdayGreetingEntity::getStudentId, studentId);
        wrapper.orderByDesc(BirthdayGreetingEntity::getGreetingDate);
        return birthdayGreetingMapper.selectList(wrapper);
    }

    public BirthdayGreetingEntity create(Long studentId, String message) {
        BirthdayGreetingEntity entity = new BirthdayGreetingEntity();
        entity.setStudentId(studentId);
        entity.setGreetingDate(LocalDate.now());
        entity.setMessage(message);
        birthdayGreetingMapper.insert(entity);
        log.info("生日祝福发送: studentId={}", studentId);
        return entity;
    }

    public void delete(Long id) {
        birthdayGreetingMapper.deleteById(id);
    }

    /** 检查今天是否已发送过祝福 */
    public boolean hasGreetedToday(Long studentId) {
        LambdaQueryWrapper<BirthdayGreetingEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BirthdayGreetingEntity::getStudentId, studentId)
               .eq(BirthdayGreetingEntity::getGreetingDate, LocalDate.now());
        return birthdayGreetingMapper.selectCount(wrapper) > 0;
    }
}
