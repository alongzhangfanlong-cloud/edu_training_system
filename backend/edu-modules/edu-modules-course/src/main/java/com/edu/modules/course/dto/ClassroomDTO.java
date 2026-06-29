package com.edu.modules.course.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ClassroomDTO {
    private Long id;
    @NotBlank(message = "教室名称不能为空")
    private String name;
    private Integer capacity;
    private String location;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private List<String> facilities;
    private Integer status;
}
