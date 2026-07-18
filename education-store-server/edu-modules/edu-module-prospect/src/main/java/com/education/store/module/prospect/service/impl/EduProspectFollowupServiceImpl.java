package com.education.store.module.prospect.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.prospect.entity.EduProspect;
import com.education.store.module.prospect.entity.EduProspectFollowup;
import com.education.store.module.prospect.mapper.EduProspectFollowupMapper;
import com.education.store.module.prospect.mapper.EduProspectMapper;
import com.education.store.module.prospect.service.IEduProspectFollowupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 跟进记录 Service 实现
 */
@Service
@RequiredArgsConstructor
public class EduProspectFollowupServiceImpl extends ServiceImpl<EduProspectFollowupMapper, EduProspectFollowup>
        implements IEduProspectFollowupService {

    private final EduProspectFollowupMapper followupMapper;
    private final EduProspectMapper prospectMapper;

    @Override
    public IPage<EduProspectFollowup> selectFollowupList(IPage<EduProspectFollowup> page, Long prospectId) {
        LambdaQueryWrapper<EduProspectFollowup> wrapper = new LambdaQueryWrapper<>();
        if (prospectId != null) {
            wrapper.eq(EduProspectFollowup::getProspectId, prospectId);
        }
        wrapper.orderByDesc(EduProspectFollowup::getFollowupDate);
        return followupMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFollowup(EduProspectFollowup followup) {
        EduProspect prospect = prospectMapper.selectById(followup.getProspectId());
        if (prospect == null) {
            throw new BusinessException("意向学员不存在");
        }

        // 插入跟进记录
        int rows = followupMapper.insert(followup);

        // 更新 prospect.last_followup_date（仅当本次跟进日期更新）
        if (followup.getFollowupDate() != null) {
            if (prospect.getLastFollowupDate() == null
                    || followup.getFollowupDate().isAfter(prospect.getLastFollowupDate())) {
                prospect.setLastFollowupDate(followup.getFollowupDate());
            }
        }

        // 首次跟进：NEW → CONTACTED 自动转换
        if ("NEW".equals(prospect.getFollowStatus())) {
            prospect.setFollowStatus("CONTACTED");
        }

        prospectMapper.updateById(prospect);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFollowup(EduProspectFollowup followup) {
        EduProspectFollowup exist = followupMapper.selectById(followup.getId());
        if (exist == null) {
            throw new BusinessException("跟进记录不存在");
        }
        // 编辑不重新触发状态转换
        return followupMapper.updateById(followup);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFollowupByIds(Long[] ids) {
        for (Long id : ids) {
            EduProspectFollowup followup = followupMapper.selectById(id);
            if (followup != null) {
                // 回退 prospect.last_followup_date 到次新记录
                LocalDateTime latest = followupMapper.selectLatestFollowupDate(
                        followup.getProspectId(), id);
                EduProspect prospect = prospectMapper.selectById(followup.getProspectId());
                if (prospect != null) {
                    prospect.setLastFollowupDate(latest);
                    prospectMapper.updateById(prospect);
                }
            }
        }
        return followupMapper.deleteBatchIds(Arrays.asList(ids));
    }
}
