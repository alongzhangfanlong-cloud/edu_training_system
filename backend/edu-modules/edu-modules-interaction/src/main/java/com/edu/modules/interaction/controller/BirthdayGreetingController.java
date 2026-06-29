package com.edu.modules.interaction.controller;

import com.edu.common.core.response.ApiResult;
import com.edu.modules.interaction.entity.BirthdayGreetingEntity;
import com.edu.modules.interaction.service.BirthdayGreetingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/birthday-greetings")
@RequiredArgsConstructor
@Tag(name = "生日祝福", description = "生日检测、祝福发送")
public class BirthdayGreetingController {

    private final BirthdayGreetingService birthdayGreetingService;

    @GetMapping
    @Operation(summary = "祝福记录列表")
    public ApiResult<Map<String, List<BirthdayGreetingEntity>>> list(
            @RequestParam(required = false) Long studentId) {
        return ApiResult.success(Map.of("list", birthdayGreetingService.list(studentId)));
    }

    @PostMapping
    @Operation(summary = "发送生日祝福")
    public ApiResult<BirthdayGreetingEntity> send(
            @RequestParam Long studentId,
            @RequestParam String message) {
        return ApiResult.success("发送成功", birthdayGreetingService.create(studentId, message));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除祝福记录")
    public ApiResult<Void> delete(@PathVariable Long id) {
        birthdayGreetingService.delete(id);
        return ApiResult.success();
    }
}
