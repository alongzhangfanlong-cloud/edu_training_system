package com.edu.app.controller;

import com.edu.common.core.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/upload")
@Tag(name = "文件上传", description = "图片/文件上传")
public class FileUploadController {

    @Value("${app.upload.dir:${user.dir}/uploads}")
    private String uploadDir;

    @PostMapping("/image")
    @Operation(summary = "上传图片（支持多文件）")
    public ApiResult<List<String>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        List<String> urls = new ArrayList<>();
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Path dateDir = Paths.get(uploadDir, "homework", dateStr);
        try {
            Files.createDirectories(dateDir);
        } catch (IOException e) {
            log.error("创建上传目录失败", e);
            return ApiResult.error(500, "上传目录创建失败");
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            String originName = file.getOriginalFilename();
            String ext = "";
            if (originName != null && originName.contains(".")) {
                ext = originName.substring(originName.lastIndexOf("."));
            }
            String newName = UUID.randomUUID().toString().replace("-", "") + ext;
            try {
                Path target = dateDir.resolve(newName);
                file.transferTo(target.toFile());
                String url = "/uploads/homework/" + dateStr + "/" + newName;
                urls.add(url);
                log.info("上传成功: {}", url);
            } catch (IOException e) {
                log.error("上传失败: {}", e.getMessage());
                return ApiResult.error(500, "上传失败: " + e.getMessage());
            }
        }
        return ApiResult.success("上传成功", urls);
    }
}
