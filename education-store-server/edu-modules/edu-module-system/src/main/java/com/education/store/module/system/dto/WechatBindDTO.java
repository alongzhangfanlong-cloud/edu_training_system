package com.education.store.module.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 微信小程序手机号绑定请求DTO
 */
@Getter
@Setter
public class WechatBindDTO {

    /** wx.login() 返回的临时登录凭证 */
    @NotBlank(message = "code不能为空")
    private String code;

    /** getPhoneNumber 返回的加密数据 */
    @NotBlank(message = "encryptedPhone不能为空")
    private String encryptedPhone;

    /** 加密算法的初始向量 */
    @NotBlank(message = "iv不能为空")
    private String iv;
}
