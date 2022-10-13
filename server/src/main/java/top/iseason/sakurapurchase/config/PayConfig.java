package top.iseason.sakurapurchase.config;

import com.lly835.bestpay.config.AliPayConfig;
import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;


@Configuration
public class PayConfig {

    @Resource
    private WechatAccountConfig accountConfig;

    @Resource
    private AliPayAccountConfig aliPayAccountConfig;

    @Bean
    public WxPayConfig wxPayConfig() {
        WxPayConfig wxPayConfig = new WxPayConfig();
        wxPayConfig.setAppId(accountConfig.getAppId());
        wxPayConfig.setMchId(accountConfig.getMchId());
        wxPayConfig.setMchKey(accountConfig.getMchKey());
        wxPayConfig.setAppSecret(accountConfig.getMiniAppSecret());

        wxPayConfig.setKeyPath(accountConfig.getKeyPath());
        wxPayConfig.setNotifyUrl(accountConfig.getNotifyUrl());
        wxPayConfig.setAppAppId(accountConfig.getAppAppId());
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
