package top.iseason.sakurapurchase.config;

import com.lly835.bestpay.config.AliPayConfig;
import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;


public class PayConfig {

    @Resource
    private WechatAccountConfig weChatAccountConfig;

    @Resource
    private AliPayAccountConfig aliPayAccountConfig;

    @Bean
    public WxPayConfig wxPayConfig() {
        WxPayConfig wxPayConfig = new WxPayConfig();
        wxPayConfig.setAppId(weChatAccountConfig.getAppId());
        wxPayConfig.setMchId(weChatAccountConfig.getMchId());
        wxPayConfig.setMchKey(weChatAccountConfig.getMchKey());
//        wxPayConfig.setAppSecret(weChatAccountConfig.getAppSecret());
        wxPayConfig.setKeyPath(weChatAccountConfig.getKeyPath());
        wxPayConfig.setNotifyUrl(weChatAccountConfig.getNotifyUrl());
        wxPayConfig.setAppAppId(weChatAccountConfig.getAppAppId());
        return wxPayConfig;
    }

    @Bean
    public AliPayConfig aliPayConfig() {
        AliPayConfig aliPayConfig = new AliPayConfig();
        aliPayConfig.setNotifyUrl(aliPayAccountConfig.getNotifyUrl());
        aliPayConfig.setAppId(aliPayAccountConfig.getAppId());
        aliPayConfig.setPrivateKey(aliPayAccountConfig.getPrivateKey());
        aliPayConfig.setAliPayPublicKey(aliPayAccountConfig.getAliPayPublicKey());
        aliPayConfig.setSandbox(aliPayAccountConfig.getSandbox());
        aliPayConfig.setReturnUrl(aliPayAccountConfig.getReturnUrl());
        return aliPayConfig;
    }

    @Bean
    public BestPayServiceImpl bestPayService(WxPayConfig wxPayConfig, AliPayConfig aliPayConfig) {
        BestPayServiceImpl bestPayService = new BestPayServiceImpl();
        bestPayService.setWxPayConfig(wxPayConfig);
        bestPayService.setAliPayConfig(aliPayConfig);
        return bestPayService;
    }
}
