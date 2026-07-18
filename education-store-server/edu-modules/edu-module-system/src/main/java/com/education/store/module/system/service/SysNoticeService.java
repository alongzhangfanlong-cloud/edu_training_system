package com.education.store.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.system.entity.system.SysNotice;

public interface SysNoticeService extends IService<SysNotice> {
    Page<SysNotice> getNoticePage(long page, long size, String keyword);
    void publishNotice(SysNotice notice, Long userId);
    void updateNotice(SysNotice notice);
    void deleteNotice(Long id);

    // ======== 系统管理 CRUD 方法 ========

    /** 分页查询通知公告列表 */
    IPage<SysNotice> selectNoticeList(IPage<SysNotice> page, SysNotice notice);

    /** 根据ID查询通知公告 */
    SysNotice selectNoticeById(Long id);

    /** 新增通知公告 */
    int insertNotice(SysNotice notice);

    /** 根据ID数组删除通知公告 */
    int deleteNoticeByIds(Long[] ids);
}
