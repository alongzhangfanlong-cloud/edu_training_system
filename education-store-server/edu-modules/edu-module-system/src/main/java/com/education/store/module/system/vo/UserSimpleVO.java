package com.education.store.module.system.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户简要 VO — 用于下拉选项等场景
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSimpleVO {

    /** 用户ID → sys_user.id */
    private Long userId;

    /** 真实姓名 */
    private String realName;
}
