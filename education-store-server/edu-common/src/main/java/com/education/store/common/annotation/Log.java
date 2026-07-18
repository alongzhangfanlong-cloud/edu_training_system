package com.education.store.common.annotation;

import com.education.store.common.enums.BusinessType;

import java.lang.annotation.*;

/**
 * 操作日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /** 操作模块 */
    String title() default "";

    /** 业务类型 */
    BusinessType businessType() default BusinessType.OTHER;

    /** 是否保存请求参数 */
    boolean isSaveRequestData() default true;

    /** 是否保存响应数据 */
    boolean isSaveResponseData() default true;
}
