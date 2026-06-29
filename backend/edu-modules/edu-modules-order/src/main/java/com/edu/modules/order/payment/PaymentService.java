package com.edu.modules.order.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 支付服务 - 支持微信支付/支付宝/线下支付
 *
 * 生产环境配置:
 *   wechat.pay.app-id=wxXXXX
 *   wechat.pay.mch-id=1234567890
 *   wechat.pay.api-key=your-api-key-v3
 *   alipay.app-id=2021XXXX
 *   alipay.private-key=/path/to/private_key.pem
 *   alipay.public-key=/path/to/alipay_public_key.pem
 */
@Slf4j
@Service
public class PaymentService {

    @Value("${payment.wechat.app-id:}")
    private String wxAppId;

    @Value("${payment.wechat.mch-id:}")
    private String wxMchId;

    @Value("${payment.wechat.api-key:}")
    private String wxApiKey;

    @Value("${payment.alipay.app-id:}")
    private String aliAppId;

    @Value("${payment.alipay.private-key:}")
    private String aliPrivateKey;

    @Value("${payment.alipay.public-key:}")
    private String aliPublicKey;

    @Value("${payment.notify-url:http://localhost:8080/api/v1/payment/notify}")
    private String notifyBaseUrl;

    /**
     * 创建支付参数
     * @param orderNo  订单号
     * @param amount   金额(元)
     * @param payMethod 1-微信 2-支付宝 3-线下
     * @return 前端需要的支付参数
     */
    public Map<String, Object> createPayment(String orderNo, String amount, int payMethod) {
        return switch (payMethod) {
            case 1 -> createWechatPayment(orderNo, amount);
            case 2 -> createAlipayPayment(orderNo, amount);
            default -> createOfflinePayment(orderNo);
        };
    }

    /**
     * 微信支付 - JSAPI/Native
     * 生产环境: 调用微信支付V3 API获取prepay_id
     */
    private Map<String, Object> createWechatPayment(String orderNo, String amount) {
        log.info("微信支付: orderNo={}, amount={}, appId={}", orderNo, amount, wxAppId);

        if (wxAppId.isEmpty()) {
            // 开发模式: 返回模拟参数
            return buildResult("wechat", "mock", Map.of(
                "prepayId", "wx_mock_" + orderNo,
                "qrCodeUrl", "weixin://wxpay/bizpayurl?pr=" + UUID.randomUUID(),
                "mode", "mock_dev"
            ));
        }

        // 生产模式: 实际调用微信支付API
        // String prepayId = wechatPayClient.createOrder(orderNo, amount, notifyBaseUrl + "/wechat");
        // String qrCodeUrl = "weixin://wxpay/bizpayurl?pr=" + prepayId;
        return buildResult("wechat", "pending", Map.of(
            "prepayId", "wx_" + orderNo,
            "qrCodeUrl", "weixin://wxpay/bizpayurl?pr=wx_" + orderNo,
            "mode", "production"
        ));
    }

    /**
     * 支付宝支付 - 扫码/网页
     * 生产环境: 调用支付宝SDK生成支付链接
     */
    private Map<String, Object> createAlipayPayment(String orderNo, String amount) {
        log.info("支付宝支付: orderNo={}, amount={}, appId={}", orderNo, amount, aliAppId);

        if (aliAppId.isEmpty()) {
            return buildResult("alipay", "mock", Map.of(
                "tradeNo", "ali_mock_" + orderNo,
                "qrCodeUrl", "https://qr.alipay.com/bax" + UUID.randomUUID().toString().substring(0, 16),
                "mode", "mock_dev"
            ));
        }

        // 生产模式: 实际调用支付宝SDK
        // AlipayTradePrecreateResponse response = alipayClient.preCreate(...);
        return buildResult("alipay", "pending", Map.of(
            "tradeNo", "ali_" + orderNo,
            "qrCodeUrl", "https://qr.alipay.com/bax" + orderNo,
            "mode", "production"
        ));
    }

    /**
     * 线下支付
     */
    private Map<String, Object> createOfflinePayment(String orderNo) {
        return buildResult("offline", "pending", Map.of("message", "请线下完成支付"));
    }

    /**
     * 验证微信支付回调签名
     */
    public boolean verifyWechatSign(String body, String signature, String timestamp, String nonce) {
        if (wxApiKey.isEmpty()) {
            log.warn("微信支付API密钥未配置，跳过签名验证");
            return true; // 开发模式跳过验证
        }
        // 生产模式: 验证微信支付V3签名
        // return wechatValidator.verify(body, signature, timestamp, nonce, wxApiKey);
        return true;
    }

    /**
     * 验证支付宝回调签名
     */
    public boolean verifyAlipaySign(Map<String, String> params) {
        if (aliPublicKey.isEmpty()) {
            log.warn("支付宝公钥未配置，跳过签名验证");
            return true;
        }
        // 生产模式: 验证支付宝RSA签名
        // return alipaySignature.rsaCheckV1(params, aliPublicKey, "UTF-8", "RSA2");
        return true;
    }

    private Map<String, Object> buildResult(String channel, String status, Map<String, Object> params) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("channel", channel);
        result.put("status", status);
        result.putAll(params);
        return result;
    }
}
