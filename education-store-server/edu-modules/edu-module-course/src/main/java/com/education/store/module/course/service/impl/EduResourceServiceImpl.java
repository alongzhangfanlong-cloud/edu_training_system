package com.education.store.module.course.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.education.store.common.exception.BusinessException;
import com.education.store.module.course.entity.EduResource;
import com.education.store.module.course.entity.EduSubject;
import com.education.store.module.course.mapper.EduResourceMapper;
import com.education.store.module.course.mapper.EduSubjectMapper;
import com.education.store.module.course.service.IEduResourceService;
import com.education.store.module.system.mapper.SysUserMapper;
import com.education.store.module.system.entity.system.SysUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 课件资料 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EduResourceServiceImpl extends ServiceImpl<EduResourceMapper, EduResource>
        implements IEduResourceService {

    private final EduResourceMapper resourceMapper;
    private final EduSubjectMapper subjectMapper;
    private final SysUserMapper userMapper;

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
        IPage<EduResource> result = resourceMapper.selectPage(page, wrapper);
        fillNamesForList(result.getRecords());
        return result;
    }

    private void fillNamesForList(List<EduResource> list) {
        if (list == null || list.isEmpty()) return;
        Set<Long> subjectIds = list.stream().map(EduResource::getSubjectId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> userIds = list.stream().map(EduResource::getUploadBy).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<Long, String> subjectMap = new HashMap<>();
        if (!subjectIds.isEmpty()) {
            List<EduSubject> subjects = subjectMapper.selectBatchIds(subjectIds);
            subjects.forEach(s -> subjectMap.put(s.getId(), s.getName()));
        }

        Map<Long, String> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<SysUser> users = userMapper.selectBatchIds(userIds);
            users.forEach(u -> userMap.put(u.getId(), StrUtil.isNotBlank(u.getNickName()) ? u.getNickName() : u.getUsername()));
        }

        for (EduResource r : list) {
            if (r.getSubjectId() != null) {
                r.setSubjectName(subjectMap.get(r.getSubjectId()));
            }
            if (r.getUploadBy() != null) {
                r.setUploadByName(userMap.get(r.getUploadBy()));
            }
        }
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

    @Override
    public java.util.Map<String, Object> getCategoryTree() {
        // 查询所有科目
        List<EduSubject> subjects = subjectMapper.selectList(new LambdaQueryWrapper<EduSubject>()
                .eq(EduSubject::getStatus, 1).orderByAsc(EduSubject::getSortOrder));
        Map<Long, String> subjectNameMap = subjects.stream()
                .collect(Collectors.toMap(EduSubject::getId, EduSubject::getName, (a,b)->a));

        // 查询所有未删除的课件
        List<EduResource> allResources = lambdaQuery().list();

        // 资源类型中文名映射
        Map<String, String> typeNameMap = new LinkedHashMap<>();
        typeNameMap.put("COURSEWARE", "课件");
        typeNameMap.put("EXERCISE", "习题");
        typeNameMap.put("TEST", "试卷");
        typeNameMap.put("VIDEO", "视频");
        typeNameMap.put("AUDIO", "音频");
        typeNameMap.put("DOCUMENT", "文档");
        typeNameMap.put("OTHER", "其他");

        // 构建三级分类树: 科目 → 年级 → 资源类型 → 数量
        Map<Long, Map<String, Map<String, Long>>> tree = new LinkedHashMap<>();

        for (EduResource r : allResources) {
            Long subjectId = r.getSubjectId() != null ? r.getSubjectId() : 0L;
            String grade = StrUtil.isNotBlank(r.getGrade()) ? r.getGrade() : "未分类";
            String type = StrUtil.isNotBlank(r.getResourceType()) ? r.getResourceType() : "OTHER";

            tree.computeIfAbsent(subjectId, k -> new LinkedHashMap<>())
                .computeIfAbsent(grade, k -> new LinkedHashMap<>())
                .merge(type, 1L, Long::sum);
        }

        // 转换为前端友好的树结构 {id, label, type, children, count}
        List<Map<String, Object>> result = new ArrayList<>();
        for (EduSubject subject : subjects) {
            Long sid = subject.getId();
            Map<String, Map<String, Long>> gradeMap = tree.getOrDefault(sid, Collections.emptyMap());
            long subjectCount = gradeMap.values().stream()
                    .flatMap(m -> m.values().stream()).mapToLong(Long::longValue).sum();

            Map<String, Object> subjectNode = new LinkedHashMap<>();
            subjectNode.put("id", sid);
            subjectNode.put("label", subject.getName());
            subjectNode.put("name", subject.getName());
            subjectNode.put("type", "subject");
            subjectNode.put("count", subjectCount);

            List<Map<String, Object>> gradeChildren = new ArrayList<>();
            for (Map.Entry<String, Map<String, Long>> gradeEntry : gradeMap.entrySet()) {
                Map<String, Object> gradeNode = new LinkedHashMap<>();
                gradeNode.put("id", sid + "_" + gradeEntry.getKey());
                gradeNode.put("label", gradeEntry.getKey());
                gradeNode.put("name", gradeEntry.getKey());
                gradeNode.put("type", "grade");
                long gradeCount = gradeEntry.getValue().values().stream().mapToLong(Long::longValue).sum();
                gradeNode.put("count", gradeCount);

                List<Map<String, Object>> typeChildren = new ArrayList<>();
                for (Map.Entry<String, Long> typeEntry : gradeEntry.getValue().entrySet()) {
                    Map<String, Object> typeNode = new LinkedHashMap<>();
                    String typeKey = typeEntry.getKey();
                    typeNode.put("id", sid + "_" + gradeEntry.getKey() + "_" + typeKey);
                    typeNode.put("label", typeNameMap.getOrDefault(typeKey, typeKey));
                    typeNode.put("name", typeNameMap.getOrDefault(typeKey, typeKey));
                    typeNode.put("value", typeKey);
                    typeNode.put("type", "resourceType");
                    typeNode.put("count", typeEntry.getValue());
                    typeChildren.add(typeNode);
                }
                gradeNode.put("children", typeChildren);
                gradeChildren.add(gradeNode);
            }
            subjectNode.put("children", gradeChildren);
            result.add(subjectNode);
        }

        // 如果有课件不属于任何科目，加一个"未分类"节点
        Map<String, Map<String, Long>> uncategorized = tree.get(0L);
        if (uncategorized != null && !uncategorized.isEmpty()) {
            Map<String, Object> uncNode = new LinkedHashMap<>();
            uncNode.put("id", 0L);
            uncNode.put("label", "未分类");
            uncNode.put("name", "未分类");
            uncNode.put("type", "subject");
            long uncCount = uncategorized.values().stream().flatMap(m->m.values().stream()).mapToLong(Long::longValue).sum();
            uncNode.put("count", uncCount);
            List<Map<String, Object>> gradeChildren = new ArrayList<>();
            for (Map.Entry<String, Map<String, Long>> ge : uncategorized.entrySet()) {
                Map<String, Object> gn = new LinkedHashMap<>();
                gn.put("id", "0_" + ge.getKey());
                gn.put("label", ge.getKey());
                gn.put("name", ge.getKey());
                gn.put("type", "grade");
                long gc = ge.getValue().values().stream().mapToLong(Long::longValue).sum();
                gn.put("count", gc);
                List<Map<String, Object>> typeChildren = new ArrayList<>();
                for (Map.Entry<String, Long> te : ge.getValue().entrySet()) {
                    Map<String, Object> tn = new LinkedHashMap<>();
                    tn.put("id", "0_" + ge.getKey() + "_" + te.getKey());
                    tn.put("label", typeNameMap.getOrDefault(te.getKey(), te.getKey()));
                    tn.put("name", typeNameMap.getOrDefault(te.getKey(), te.getKey()));
                    tn.put("value", te.getKey());
                    tn.put("type", "resourceType");
                    tn.put("count", te.getValue());
                    typeChildren.add(tn);
                }
                gn.put("children", typeChildren);
                gradeChildren.add(gn);
            }
            uncNode.put("children", gradeChildren);
            result.add(uncNode);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("tree", result);
        response.put("totalCount", allResources.size());
        return response;
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
