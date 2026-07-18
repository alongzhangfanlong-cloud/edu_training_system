package com.education.store.module.system.controller;

import com.education.store.common.model.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Tag(name = "文件管理")
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    @Value("${edu.upload-path:upload}")
    private String uploadPath;

    @PostMapping("/upload")
    public Result<Map<String, Object>> upload(Authentication auth, @RequestParam("file") MultipartFile file) throws IOException {
        Long userId = (Long) auth.getPrincipal();
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

    @GetMapping("/{dateDir}/{storedName}")
    public ResponseEntity<Resource> download(@PathVariable String dateDir, @PathVariable String storedName,
            @RequestParam(required = false) String name) throws IOException {
        Path path = Paths.get(uploadPath, dateDir, storedName);
        if (!Files.exists(path)) return ResponseEntity.notFound().build();
        Resource resource = new FileSystemResource(path);
        String downloadName = name != null ? name : storedName;
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + URLEncoder.encode(downloadName, StandardCharsets.UTF_8) + "\"")
                .body(resource);
    }
}
