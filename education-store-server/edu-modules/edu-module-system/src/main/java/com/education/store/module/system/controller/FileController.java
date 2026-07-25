package com.education.store.module.system.controller;

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
 * <p>安全注意：
 * <ul>
 *   <li>上传和下载均需认证身份</li>
 *   <li>下载路径做了路径遍历防护</li>
 *   <li>上传不做类型限制（由调用方业务层校验），此接口为通用文件存储</li>
 * </ul>
 */
@Tag(name = "文件管理")
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

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
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
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
