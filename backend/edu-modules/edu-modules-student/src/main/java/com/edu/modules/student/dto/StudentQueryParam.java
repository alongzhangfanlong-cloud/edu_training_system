package com.edu.modules.student.dto;

import com.edu.common.mybatis.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class StudentQueryParam extends PageParam {
    private String keyword;
    private Integer status;
    private Long classId;
}
