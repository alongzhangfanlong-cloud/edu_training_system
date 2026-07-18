package com.education.store.module.course.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.course.entity.EduResource;
import com.education.store.module.course.mapper.EduResourceMapper;
import com.education.store.module.course.service.IEduResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

/**
 * 课件资料 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EduResourceServiceImpl extends ServiceImpl<EduResourceMapper, EduResource>
        implements IEduResourceService {

    private final EduResourceMapper resourceMapper;

    @Value("${edu.file.upload-path:./uploads/resources}")
    private String uploadBasePath;

    /** 允许上传的文件扩展名 */
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",           // 文档
            "jpg", "jpeg", "png", "gif", "bmp", "svg", "webp",           // 图片
            "mp4", "avi", "mov", "wmv", "flv", "mkv", "webm",           // 视频
            "mp3", "wav", "aac", "flac", "ogg",                          // 音频
            "txt", "csv", "zip", "rar", "7z"                              // 其他
    );

    /** 文件大小上限：50MB */
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024L;

    @Override
    public IPage<EduResource> selectResourceList(IPage<EduResource> page, EduResource resource) {
        LambdaQueryWrapper<EduResource> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(resource.getName())) {
            wrapper.like(EduResource::getName, resource.getName());
        }
        if (resource.getSubjectId() != null) {
            wrapper.eq(EduResource::getSubjectId, resource.getSubjectId());
        }
        if (StrUtil.isNotBlank(resource.getResourceType())) {
            wrapper.eq(EduResource::getResourceType, resource.getResourceType());
        }
        if (StrUtil.isNotBlank(resource.getGrade())) {
            wrapper.eq(EduResource::getGrade, resource.getGrade());
        }
        if (resource.getStatus() != null) {
            wrapper.eq(EduResource::getStatus, resource.getStatus());
        }
        wrapper.orderByDesc(EduResource::getCreateTime);
        return resourceMapper.selectPage(page, wrapper);
    }

    @Override
    public EduResource selectResourceById(Long id) {
        return resourceMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertResource(EduResource resource, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        validateFile(file);
        // 存储文件
        saveFileToDisk(resource, file);
        return resourceMapper.insert(resource);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateResource(EduResource resource, MultipartFile file) {
        EduResource exist = resourceMapper.selectById(resource.getId());
        if (exist == null) {
            throw new BusinessException("课件不存在");
        }

        // 如果上传了新文件，先存新文件成功后再删旧文件
        if (file != null && !file.isEmpty()) {
            validateFile(file);
            String oldFilePath = exist.getFilePath();
            saveFileToDisk(resource, file);
            deletePhysicalFile(oldFilePath);
        } else {
            // 保留原文件信息
            resource.setFileName(exist.getFileName());
            resource.setFilePath(exist.getFilePath());
            resource.setFileUrl(exist.getFileUrl());
            resource.setFileSize(exist.getFileSize());
            resource.setFileType(exist.getFileType());
        }

        return resourceMapper.updateById(resource);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteResourceByIds(Long[] ids) {
        // 先查文件路径（DB 删除前），再删 DB，最后清理物理文件
        java.util.Map<Long, String> filePathMap = new java.util.LinkedHashMap<>();
        for (Long id : ids) {
            EduResource resource = resourceMapper.selectById(id);
            if (resource != null && resource.getFilePath() != null) {
                filePathMap.put(id, resource.getFilePath());
            }
        }
        // 先删除数据库记录
        int rows = resourceMapper.deleteBatchIds(Arrays.asList(ids));
        // DB 删除成功后再清理物理文件（失败不影响事务，仅日志警告）
        for (String filePath : filePathMap.values()) {
            deletePhysicalFile(filePath);
        }
        return rows;
    }

    @Override
    public void incrementDownloadCount(Long id) {
        resourceMapper.incrementDownloadCount(id);
    }

    // ========== 私有方法 ==========

    /** 校验文件类型和大小 */
    private void validateFile(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("文件大小不能超过 " + (MAX_FILE_SIZE / 1024 / 1024) + "MB");
        }
        String originalName = file.getOriginalFilename();
        if (originalName != null) {
            String ext = FileUtil.extName(originalName).toLowerCase();
            if (!ALLOWED_EXTENSIONS.contains(ext)) {
                throw new BusinessException("不支持的文件类型: ." + ext + "，允许的类型: " + ALLOWED_EXTENSIONS);
            }
        }
    }

    /** 存储文件到磁盘，填充 resource 的文件字段 */
    private void saveFileToDisk(EduResource resource, MultipartFile file) {
        try {
            // 生成存储路径：uploads/resources/yyyyMM/uuid.ext
            String dateDir = java.time.LocalDate.now().toString().replace("-", "");
            String ext = FileUtil.extName(file.getOriginalFilename());
            String storedName = UUID.randomUUID().toString().replace("-", "") + "." + ext;
            String relativePath = dateDir + "/" + storedName;

            File destFile = Paths.get(uploadBasePath, relativePath).toFile();
            FileUtil.mkParentDirs(destFile);
            file.transferTo(destFile);

            resource.setFileName(file.getOriginalFilename());
            resource.setFilePath(destFile.getAbsolutePath());
            resource.setFileUrl("/files/resources/" + relativePath);
            resource.setFileSize(file.getSize());
            resource.setFileType(file.getContentType());
        } catch (IOException e) {
            log.error("文件存储失败", e);
            throw new BusinessException("文件存储失败: " + e.getMessage());
        }
    }

    /** 删除物理文件（磁盘清理，失败不影响事务） */
    private void deletePhysicalFile(String filePath) {
        if (StrUtil.isBlank(filePath)) {
            return;
        }
        try {
            File file = new File(filePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    log.warn("物理文件删除失败: {}", filePath);
                }
            }
        } catch (Exception e) {
            log.warn("物理文件删除异常: {}", filePath, e);
        }
    }
}
