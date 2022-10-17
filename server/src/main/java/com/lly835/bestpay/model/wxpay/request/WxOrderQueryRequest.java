package com.lly835.bestpay.model.wxpay.request;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Created by 廖师兄
 * 2018-05-31 17:47
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml")
public class WxOrderQueryRequest {

    @XmlElement(name = "appid")
    private String appid;

    @XmlElement(name = "mch_id")
    private String mchId;

    @XmlElement(name = "transaction_id", required = false)
    private String transactionId;

    @XmlElement(name = "out_trade_no", required = false)
    private String outTradeNo;

    @XmlElement(name = "nonce_str")
    private String nonceStr;

    @XmlElement(name = "sign")
    private String sign;

    @XmlElement(name = "sign_type", required = false)
    private String signType;
}
