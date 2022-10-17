package com.lly835.bestpay.model.wxpay.request;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Created by 廖师兄
 * 2017-07-02 13:42
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml")
public class WxPayUnifiedorderRequest {

    @XmlElement(name = "appid")
    private String appid;

    @XmlElement(name = "mch_id")
    private String mchId;

    @XmlElement(name = "nonce_str")
    private String nonceStr;

    @XmlElement(name = "sign")
    private String sign;

    @XmlElement(name = "attach", required = false)
    private String attach;

    @XmlElement(name = "body", required = false)
    private String body;

    @XmlElement(name = "detail", required = false)
    private String detail;

    @XmlElement(name = "notify_url")
    private String notifyUrl;

    @XmlElement(name = "openid", required = false)
    private String openid;

    @XmlElement(name = "out_trade_no")
    private String outTradeNo;

    @XmlElement(name = "spbill_create_ip")
    private String spbillCreateIp;

    @XmlElement(name = "total_fee")
    private Integer totalFee;

    @XmlElement(name = "trade_type")
    private String tradeType;

    @XmlElement(name = "auth_code", required = false)
    private String authCode;
}
