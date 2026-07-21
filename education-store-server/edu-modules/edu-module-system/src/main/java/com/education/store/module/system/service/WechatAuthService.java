package com.education.store.module.system.service;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import com.education.store.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 微信小程序认证服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WechatAuthService {

    private final WxMaService wxMaService;

    /**
     * 用 wx.login() 返回的 code 换取 openid
     *
     * @param code wx.login() 返回的临时登录凭证
     * @return openid
     */
    public String getOpenid(String code) {
        try {
            WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);
            log.debug("微信登录成功，openid={}", session.getOpenid());
            return session.getOpenid();
        } catch (Exception e) {
            log.error("微信 code2session 失败: {}", e.getMessage(), e);
            throw new BusinessException("微信登录失败，请稍后重试");
        }
    }

    /**
     * 解密微信手机号
     *
     * @param code          wx.login() 返回的 code（用于获取 session_key）
     * @param encryptedData getPhoneNumber 返回的加密数据
     * @param iv            加密算法的初始向量
     * @return 解密后的纯手机号（不含国家代码）
     */
    public String decryptPhone(String code, String encryptedData, String iv) {
        try {
            // 先获取 session_key
            WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);
            // 解密手机号
            WxMaPhoneNumberInfo phoneInfo = wxMaService.getUserService()
                    .getPhoneNoInfo(session.getSessionKey(), encryptedData, iv);
            String phone = phoneInfo.getPurePhoneNumber();
            if (phone == null || phone.isEmpty()) {
                phone = phoneInfo.getPhoneNumber(); // 降级取带国家代码的
            }
            log.debug("微信手机号解密成功，phone={}", phone);
            return phone;
        } catch (Exception e) {
            log.error("微信手机号解密失败: {}", e.getMessage(), e);
            throw new BusinessException("手机号授权失败，请重试");
        }
    }
}
