package com.education.store.common.constant;

/**
 * 系统常量
 */
public interface Constants {

    /** 超级管理员角色编码 */
    String ROLE_ADMIN = "admin";

    /** 普通用户角色编码 */
    String ROLE_USER = "user";

    /** Token 前缀 */
    String TOKEN_PREFIX = "Bearer ";

    /** JWT Claim - 用户ID */
    String CLAIM_USER_ID = "userId";

    /** JWT Claim - 用户名 */
    String CLAIM_USERNAME = "username";

    /** JWT Claim - Token类型 */
    String CLAIM_TOKEN_TYPE = "type";

    /** Token类型 - Access */
    String TOKEN_TYPE_ACCESS = "access";

    /** Token类型 - Refresh */
    String TOKEN_TYPE_REFRESH = "refresh";

    /** Redis Key前缀 - 用户Token */
    String REDIS_KEY_TOKEN = "auth:token:";

    /** Redis Key前缀 - 用户权限 */
    String REDIS_KEY_PERM = "auth:perm:";

    /** Redis Key前缀 - 验证码 */
    String REDIS_KEY_CAPTCHA = "captcha:";

    /** Redis Key前缀 - 菜单树 */
    String REDIS_KEY_MENU_TREE = "sys:menu:tree";

    /** 状态 - 正常 */
    Integer STATUS_ENABLE = 1;

    /** 状态 - 禁用 */
    Integer STATUS_DISABLE = 0;
}
