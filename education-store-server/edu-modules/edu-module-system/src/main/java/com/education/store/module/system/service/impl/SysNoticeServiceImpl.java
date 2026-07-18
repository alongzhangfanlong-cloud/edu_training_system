package com.education.store.module.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.system.entity.system.SysNotice;
import com.education.store.module.system.entity.system.SysUser;
import com.education.store.module.system.mapper.SysNoticeMapper;
import com.education.store.module.system.mapper.SysUserMapper;
import com.education.store.module.system.service.SysNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 通知公告 Service 实现
 */
@Service
@RequiredArgsConstructor
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice>
        implements SysNoticeService {

    private final SysNoticeMapper noticeMapper;
    private final SysUserMapper userMapper;

    // ======== 原有方法 ========

    @Override
    public Page<SysNotice> getNoticePage(long page, long size, String keyword) {
        LambdaQueryWrapper<SysNotice> w = new LambdaQueryWrapper<>();
        w.eq(SysNotice::getStatus, 1);
        if (StrUtil.isNotBlank(keyword)) w.like(SysNotice::getTitle, keyword);
        w.orderByDesc(SysNotice::getCreateTime);
        return baseMapper.selectPage(new Page<>(page, size), w);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishNotice(SysNotice notice, Long userId) {
        if (notice.getStatus() != null && notice.getStatus() == 1) {
            notice.setPublishBy(userId);
            notice.setPublishTime(LocalDateTime.now());
        }
        if (notice.getId() != null) {
            baseMapper.updateById(notice);
        } else {
            baseMapper.insert(notice);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNotice(SysNotice notice) {
        SysNotice exist = baseMapper.selectById(notice.getId());
        if (exist == null) throw new BusinessException("公告不存在");
        // 发布时设置发布人和发布时间
        if (notice.getStatus() != null && notice.getStatus() == 1
                && (exist.getStatus() == null || exist.getStatus() == 0)) {
            notice.setPublishTime(LocalDateTime.now());
        }
        baseMapper.updateById(notice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotice(Long id) {
        baseMapper.deleteById(id);
    }

    // ======== 系统管理 CRUD 方法 ========

    @Override
    public IPage<SysNotice> selectNoticeList(
            IPage<SysNotice> page,
            SysNotice notice) {
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(notice.getTitle())) {
            wrapper.like(SysNotice::getTitle, notice.getTitle());
        }
        if (StrUtil.isNotBlank(notice.getType())) {
            wrapper.eq(SysNotice::getType, notice.getType());
        }
        if (notice.getStatus() != null) {
            wrapper.eq(SysNotice::getStatus, notice.getStatus());
        }
        wrapper.orderByDesc(SysNotice::getCreateTime);
        IPage<SysNotice> result = noticeMapper.selectPage(page, wrapper);

        // 填充发布人名称
        List<SysNotice> records = result.getRecords();
        if (records != null && !records.isEmpty()) {
            List<Long> publisherIds = records.stream()
                    .map(SysNotice::getPublishBy)
                    .filter(id -> id != null)
                    .distinct()
                    .collect(Collectors.toList());
            if (!publisherIds.isEmpty()) {
                List<SysUser> users = userMapper.selectBatchIds(publisherIds);
                Map<Long, String> nameMap = users.stream()
                        .collect(Collectors.toMap(SysUser::getId,
                                u -> u.getRealName() != null ? u.getRealName() : u.getUsername()));
                records.forEach(r -> {
                    if (r.getPublishBy() != null) {
                        r.setPublisherName(nameMap.getOrDefault(r.getPublishBy(), ""));
                    }
                });
            }
        }
        return result;
    }

    @Override
    public SysNotice selectNoticeById(Long id) {
        SysNotice notice = noticeMapper.selectById(id);
        if (notice != null && notice.getPublishBy() != null) {
            SysUser user = userMapper.selectById(notice.getPublishBy());
            if (user != null) {
                notice.setPublisherName(user.getRealName() != null ? user.getRealName() : user.getUsername());
            }
        }
        return notice;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertNotice(SysNotice notice) {
        return noticeMapper.insert(notice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteNoticeByIds(Long[] ids) {
        return noticeMapper.deleteBatchIds(Arrays.asList(ids));
    }
}
