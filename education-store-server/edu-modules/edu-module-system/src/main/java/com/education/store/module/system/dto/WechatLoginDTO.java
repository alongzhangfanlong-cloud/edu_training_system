package com.education.store.module.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 微信小程序静默登录请求DTO
 */
@Getter
@Setter
public class WechatLoginDTO {

    /** wx.login() 返回的临时登录凭证 */
    @NotBlank(message = "code不能为空")
    private String code;
}
