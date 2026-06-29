package com.edu.common.security;

import java.lang.annotation.*;

/**
 * 在Controller方法参数上注入当前登录用户
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {
}
