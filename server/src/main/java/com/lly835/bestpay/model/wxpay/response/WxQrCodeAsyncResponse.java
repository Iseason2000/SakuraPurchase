package com.lly835.bestpay.model.wxpay.response;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * 微信扫码异步调用请求
 * Created by steven ma
 * 2019/9/10 17:56
 */

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml")
public class WxQrCodeAsyncResponse {

    @XmlElement(name = "appid", required = false)
    private String appid;

    @XmlElement(name = "openid", required = false)
    private String openId;

    @XmlElement(name = "mch_id", required = false)
    private String mchId;

    @XmlElement(name = "is_subscribe", required = false)
    private String isSubscribe;

    @XmlElement(name = "nonce_str", required = false)
    private String nonceStr;

    @XmlElement(name = "product_id", required = false)
    private String productId; //商品ID

    @XmlElement(name = "sign", required = false)
    private String sign;

}
