package com.education.store.module.system.dto;

import lombok.Data;

/**
 * 用户列表查询参数
 */
@Data
public class UserQueryDTO {

    /** 搜索关键词（用户名/姓名/手机号） */
    private String keyword;

    /** 状态（1正常 0停用） */
    private Integer status;

    /** 页码（默认1） */
    private Integer page = 1;

    /** 每页条数（默认10） */
    private Integer size = 10;
}
