package com.education.store.module.system.controller;

import com.education.store.common.exception.BusinessException;
import com.education.store.common.model.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 文件管理控制器 — 通用文件上传/下载
 *
 * <p>安全防护：
 * <ul>
 *   <li>上传和下载均需认证身份</li>
 *   <li>文件类型白名单校验（仅允许图片/文档/常见安全格式）</li>
 *   <li>文件大小限制（单文件最大20MB）</li>
 *   <li>UUID重命名防止路径遍历和文件名注入</li>
 *   <li>下载路径二次校验，防止目录遍历攻击</li>
 * </ul>
 */
@Tag(name = "文件管理")
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    /** 允许上传的文件扩展名白名单 */
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp",  // 图片
            ".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx",  // 文档
            ".txt", ".csv", ".zip", ".rar", ".7z",  // 文本/压缩包
            ".mp3", ".mp4", ".wav", ".avi", ".mov"  // 音视频
    );

    /** 单文件最大大小：20MB */
    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024;

    @Value("${edu.upload-path:upload}")
    private String uploadPath;

    /**
     * 将相对路径的上传目录解析为绝对路径，避免因 Tomcat/JVM 工作目录变化导致文件写入错误位置
     */
    @PostConstruct
    private void resolveUploadPath() {
        Path absolute = Paths.get(uploadPath).toAbsolutePath().normalize();
        this.uploadPath = absolute.toString();
    }

    @Operation(summary = "文件上传")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/upload")
    public Result<Map<String, Object>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        // 文件大小校验
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("文件大小不能超过20MB");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new BusinessException("文件名不能为空");
        }

        // 文件扩展名白名单校验
        String ext = "";
        int dotIndex = originalName.lastIndexOf(".");
        if (dotIndex > 0) {
            ext = originalName.substring(dotIndex).toLowerCase(Locale.ROOT);
        }
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new BusinessException("不支持的文件类型，仅允许上传图片、文档、音视频等常见格式");
        }

        // 防止双后缀攻击（如 shell.jsp.png）
        String lowerName = originalName.toLowerCase(Locale.ROOT);
        for (String dangerous : new String[]{".jsp", ".jspx", ".php", ".asp", ".aspx", ".exe", ".sh", ".bat", ".cmd", ".html", ".htm"}) {
            if (lowerName.contains(dangerous + ".")) {
                throw new BusinessException("文件名包含非法字符");
            }
        }

        String storedName = UUID.randomUUID().toString().replace("-", "") + ext;
        String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        Path dir = Paths.get(uploadPath, dateDir);
        Files.createDirectories(dir);
        Path target = dir.resolve(storedName);
        file.transferTo(target.toFile());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("fileName", originalName);
        result.put("filePath", dateDir + "/" + storedName);
        result.put("fileSize", file.getSize());
        result.put("fileType", ext);
        result.put("storedName", storedName);
        return Result.success(result);
    }

    @Operation(summary = "文件下载")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{dateDir}/{storedName}")
    public ResponseEntity<Resource> download(@PathVariable String dateDir, @PathVariable String storedName,
            @RequestParam(required = false) String name) throws IOException {
        // 路径遍历防护：禁止 .. 和绝对路径
        if (dateDir.contains("..") || storedName.contains("..")
                || dateDir.contains("/") || storedName.contains("/")
                || dateDir.contains("\\") || storedName.contains("\\")) {
            return ResponseEntity.badRequest().build();
        }

        Path uploadRoot = Paths.get(uploadPath).toAbsolutePath().normalize();
        Path path = uploadRoot.resolve(dateDir).resolve(storedName).normalize();

        // 二次确认：解析后的路径必须在 upload 目录内
        if (!path.startsWith(uploadRoot)) {
            return ResponseEntity.badRequest().build();
        }

        if (!Files.exists(path) || !Files.isReadable(path)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(path);
        String downloadName = name != null ? name : storedName;
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + URLEncoder.encode(downloadName, StandardCharsets.UTF_8) + "\"")
                .body(resource);
    }
}
