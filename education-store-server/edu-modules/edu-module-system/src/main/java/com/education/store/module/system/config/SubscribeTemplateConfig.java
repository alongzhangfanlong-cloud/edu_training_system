package com.education.store.module.system.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * 微信订阅消息模板配置
 * <p>绑定到 wechat.mini-app 前缀（与 WechatConfig 共享），读取 subscribeTemplates 子属性</p>
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "wechat.mini-app")
public class SubscribeTemplateConfig {
    /** 订阅消息模板ID映射，key=通知类型，value=微信模板ID */
    private Map<String, String> subscribeTemplates;
}
