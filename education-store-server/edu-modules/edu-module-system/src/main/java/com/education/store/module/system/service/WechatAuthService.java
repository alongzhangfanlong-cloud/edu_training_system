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
 *
 * <p>重要：wx.login() 返回的 code 是一次性的，每个 code 只能调用一次 code2session。
 * 因此涉及 code 的所有操作必须在同一个 session 上下文中完成。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WechatAuthService {

    private final WxMaService wxMaService;

    /**
     * 用 wx.login() 返回的 code 换取 session 信息（一次性操作！）
     *
     * @param code wx.login() 返回的临时登录凭证
     * @return session 信息（含 openid 和 sessionKey）
     */
    public WxMaJscode2SessionResult getSessionInfo(String code) {
        try {
            WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);
            log.debug("微信 code2session 成功，openid={}", session.getOpenid());
            return session;
        } catch (Exception e) {
            log.error("微信 code2session 失败: {}", e.getMessage(), e);
            throw new BusinessException("微信登录失败，请稍后重试");
        }
    }

    /**
     * 用 wx.login() 返回的 code 换取 openid
     *
     * @param code wx.login() 返回的临时登录凭证
     * @return openid
     */
    public String getOpenid(String code) {
        return getSessionInfo(code).getOpenid();
    }

    /**
     * 解密微信手机号（使用已有的 sessionKey，不消耗 code）
     *
     * @param sessionKey    从 code2session 获取的 session_key
     * @param encryptedData getPhoneNumber 返回的加密数据
     * @param iv            加密算法的初始向量
     * @return 解密后的纯手机号（不含国家代码）
     */
    public String decryptPhone(String sessionKey, String encryptedData, String iv) {
        try {
            WxMaPhoneNumberInfo phoneInfo = wxMaService.getUserService()
                    .getPhoneNoInfo(sessionKey, encryptedData, iv);
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
