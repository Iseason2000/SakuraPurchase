package com.lly835.bestpay.model.wxpay.request;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml")
public class WxOrderCloseRequest {
    @XmlElement(name = "appid", required = true)
    private String appid;

    @XmlElement(name = "mch_id", required = true)
    private String mchId;

    @XmlElement(name = "out_trade_no", required = true)
    private String outTradeNo;

    @XmlElement(name = "nonce_str", required = true)
    private String nonceStr;

    @XmlElement(name = "sign", required = true)
    private String sign;

    @XmlElement(name = "sign_type")
    private String signType;
}
