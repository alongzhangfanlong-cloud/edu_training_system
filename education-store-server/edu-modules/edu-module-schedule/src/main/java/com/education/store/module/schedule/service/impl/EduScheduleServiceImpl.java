package com.education.store.module.schedule.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.schedule.entity.EduSchedule;
import com.education.store.module.schedule.mapper.EduScheduleMapper;
import com.education.store.module.schedule.service.IEduScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class EduScheduleServiceImpl extends ServiceImpl<EduScheduleMapper, EduSchedule>
        implements IEduScheduleService {

    private final EduScheduleMapper scheduleMapper;

    @Override
    public IPage<EduSchedule> selectScheduleList(IPage<EduSchedule> page, EduSchedule schedule) {
        LambdaQueryWrapper<EduSchedule> wrapper = new LambdaQueryWrapper<>();
        if (schedule.getClassId() != null) wrapper.eq(EduSchedule::getClassId, schedule.getClassId());
        if (schedule.getTeacherId() != null) wrapper.eq(EduSchedule::getTeacherId, schedule.getTeacherId());
        if (schedule.getRoomId() != null) wrapper.eq(EduSchedule::getRoomId, schedule.getRoomId());
        if (StrUtil.isNotBlank(schedule.getStatus())) wrapper.eq(EduSchedule::getStatus, schedule.getStatus());
        wrapper.orderByDesc(EduSchedule::getScheduleDate);
        return scheduleMapper.selectPage(page, wrapper);
    }

    @Override public EduSchedule selectScheduleById(Long id) { return scheduleMapper.selectById(id); }

    @Override @Transactional(rollbackFor = Exception.class)
    public int insertSchedule(EduSchedule schedule) {
        if (!checkConflict(schedule)) throw new BusinessException("排课冲突，请检查教师/教室/班级时间安排");
        schedule.setStatus("NORMAL");
        if (StrUtil.isBlank(schedule.getScheduleType())) schedule.setScheduleType("NORMAL");
        return scheduleMapper.insert(schedule);
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int updateSchedule(EduSchedule schedule) {
        EduSchedule exist = scheduleMapper.selectById(schedule.getId());
        if (exist == null) throw new BusinessException("排课记录不存在");
        return scheduleMapper.updateById(schedule);
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int deleteScheduleByIds(Long[] ids) {
        return scheduleMapper.deleteBatchIds(Arrays.asList(ids));
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int cancelSchedule(Long id, String reason) {
        EduSchedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) throw new BusinessException("排课记录不存在");
        if (!"NORMAL".equals(schedule.getStatus()) && !"MAKEUP".equals(schedule.getStatus()))
            throw new BusinessException("仅正常/补课状态的排课可取消");
        schedule.setStatus("CANCELLED");
        schedule.setCancelReason(reason);
        schedule.setCancelTime(LocalDateTime.now());
        return scheduleMapper.updateById(schedule);
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int reschedule(EduSchedule updated) {
        EduSchedule exist = scheduleMapper.selectById(updated.getId());
        if (exist == null) throw new BusinessException("排课记录不存在");
        exist.setStatus("RESCHEDULED");
        scheduleMapper.updateById(exist);

        EduSchedule newSchedule = new EduSchedule();
        newSchedule.setPlanId(exist.getPlanId());
        newSchedule.setClassId(updated.getClassId() != null ? updated.getClassId() : exist.getClassId());
        newSchedule.setCourseId(exist.getCourseId());
        newSchedule.setTeacherId(updated.getTeacherId() != null ? updated.getTeacherId() : exist.getTeacherId());
        newSchedule.setRoomId(updated.getRoomId() != null ? updated.getRoomId() : exist.getRoomId());
        newSchedule.setScheduleDate(updated.getScheduleDate());
        newSchedule.setStartTime(updated.getStartTime());
        newSchedule.setEndTime(updated.getEndTime());
        newSchedule.setStatus("NORMAL");
        newSchedule.setScheduleType(exist.getScheduleType());
        newSchedule.setRescheduledFromId(exist.getId());
        return scheduleMapper.insert(newSchedule);
    }

    @Override @Transactional(rollbackFor = Exception.class)
    public int consumeSchedule(Long id) {
        EduSchedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) throw new BusinessException("排课记录不存在");
        schedule.setConsumed(1);
        schedule.setConsumeTime(LocalDateTime.now());
        return scheduleMapper.updateById(schedule);
    }

    @Override
    public boolean checkConflict(EduSchedule schedule) {
        if (schedule.getTeacherId() != null) {
            int c = scheduleMapper.countTeacherConflict(schedule.getTeacherId(), schedule.getScheduleDate(), schedule.getStartTime(), schedule.getEndTime());
            if (c > 0) return false;
        }
        if (schedule.getRoomId() != null) {
            int c = scheduleMapper.countRoomConflict(schedule.getRoomId(), schedule.getScheduleDate(), schedule.getStartTime(), schedule.getEndTime());
            if (c > 0) return false;
        }
        if (schedule.getClassId() != null) {
            int c = scheduleMapper.countClassConflict(schedule.getClassId(), schedule.getScheduleDate(), schedule.getStartTime(), schedule.getEndTime());
            if (c > 0) return false;
        }
        return true;
    }
}
