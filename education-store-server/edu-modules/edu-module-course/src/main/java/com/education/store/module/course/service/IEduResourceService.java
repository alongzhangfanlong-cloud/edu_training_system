package com.education.store.module.course.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.education.store.module.course.entity.EduResource;
import org.springframework.web.multipart.MultipartFile;

/**
 * 课件资料 Service 接口
 */
public interface IEduResourceService extends IService<EduResource> {

    /** 分页查询课件列表 */
    IPage<EduResource> selectResourceList(IPage<EduResource> page, EduResource resource);

    /** 根据ID查询课件 */
    EduResource selectResourceById(Long id);

    /** 上传课件（含文件存储） */
    int insertResource(EduResource resource, MultipartFile file);

    /** 更新课件信息（可选替换文件） */
    int updateResource(EduResource resource, MultipartFile file);

    /** 根据ID数组删除课件（含物理文件清理） */
    int deleteResourceByIds(Long[] ids);

    /** 原子增加下载次数 */
    void incrementDownloadCount(Long id);
}
