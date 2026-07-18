package com.education.store.module.tuition.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 赠课新增 DTO — 仅暴露允许客户端提交的字段
 */
@Data
public class GiftHoursCreateDTO {

    @NotNull(message = "学员ID不能为空")
    private Long studentId;

    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    @NotNull(message = "赠送课时不能为空")
    @Min(value = 1, message = "赠送课时至少为1")
    private Integer hours;

    private String reason;

    private String reasonCustom;

    private Long operatorId;

    private String remark;
}
