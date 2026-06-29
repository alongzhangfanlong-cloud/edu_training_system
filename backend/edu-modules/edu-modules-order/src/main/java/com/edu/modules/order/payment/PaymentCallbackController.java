package com.edu.modules.order.payment;

import com.edu.modules.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 支付回调接口 - 接收微信/支付宝的异步通知
 *
 * 生产环境需配置:
 *   微信支付回调URL: https://your-domain.com/api/v1/payment/notify/wechat
 *   支付宝回调URL:   https://your-domain.com/api/v1/payment/notify/alipay
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Tag(name = "支付回调", description = "微信/支付宝异步通知接收")
public class PaymentCallbackController {

    private final PaymentService paymentService;
    private final OrderService orderService;

    /**
     * 微信支付回调 - POST JSON
     */
    @PostMapping("/notify/wechat")
    @Operation(summary = "微信支付异步通知")
    public Map<String, String> wechatNotify(
            @RequestBody String body,
            @RequestHeader("Wechatpay-Signature") String signature,
            @RequestHeader("Wechatpay-Timestamp") String timestamp,
            @RequestHeader("Wechatpay-Nonce") String nonce) {

        log.info("收到微信支付回调: timestamp={}, nonce={}", timestamp, nonce);

        if (!paymentService.verifyWechatSign(body, signature, timestamp, nonce)) {
            log.error("微信支付签名验证失败");
            return Map.of("code", "FAIL", "message", "签名验证失败");
        }

        // 解析回调body, 获取订单号, 更新订单状态
        // String orderNo = parseOrderNo(body);
        // orderService.confirmPayment(orderNo, "wechat_transaction_id");
        log.info("微信支付回调处理完成");
        return Map.of("code", "SUCCESS", "message", "OK");
    }

    /**
     * 支付宝回调 - POST FORM
     */
    @PostMapping("/notify/alipay")
    @Operation(summary = "支付宝异步通知")
    public String alipayNotify(@RequestParam Map<String, String> params) {
        log.info("收到支付宝回调: tradeNo={}", params.get("out_trade_no"));

        if (!paymentService.verifyAlipaySign(params)) {
            log.error("支付宝签名验证失败");
            return "fail";
        }

        String tradeStatus = params.get("trade_status");
        if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
            // 更新订单状态
            // orderService.confirmPayment(params.get("out_trade_no"), params.get("trade_no"));
            log.info("支付宝支付成功: tradeNo={}", params.get("out_trade_no"));
        }

        return "success";
    }
}
