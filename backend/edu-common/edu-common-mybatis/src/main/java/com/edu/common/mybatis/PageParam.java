package com.edu.common.mybatis;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PageParam {

    @Min(value = 1, message = "页码最小为1")
    private Integer page = 1;

    @Min(value = 1, message = "每页数量最小为1")
    @Max(value = 100, message = "每页数量最大为100")
    private Integer pageSize = 20;

    public int offset() {
        return (page - 1) * getSafePageSize();
    }

    /**
     * 获取安全的pageSize（兜底保护，即使绕过@Valid也能限制在100以内）
     */
    public int getSafePageSize() {
        if (pageSize == null || pageSize < 1) return 20;
        return Math.min(pageSize, 100);
    }
}
