package com.education.store.module.dashboard.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 欢迎区信息 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户姓名 */
    private String userName;

    /** 上次登录时间 yyyy-MM-dd HH:mm */
    private String lastLoginTime;

    /** 是否首次登录 */
    private Boolean isFirstLogin;

    /** 服务器日期 yyyy-MM-dd */
    private String serverDate;

    /** 星期中文名 */
    private String serverDayOfWeek;

    /** 服务器日期格式化文本 */
    private String serverDateFormatted;
}
