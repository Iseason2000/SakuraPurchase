package com.lly835.bestpay.model.wxpay.response;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * 同步返回参数
 * Created by 廖师兄
 * 2017-07-02 13:46
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml")
public class WxPaySyncResponse {
    @XmlElement(name = "return_code")
    private String returnCode;

    @XmlElement(name = "return_msg", required = false)
    private String returnMsg;

    /**
     * 以下字段在return_code为SUCCESS的时候有返回.
     */
    @XmlElement(name = "appid", required = false)
    private String appid;

    @XmlElement(name = "mch_id", required = false)
    private String mchId;

    @XmlElement(name = "device_info", required = false)
    private String deviceInfo;

    @XmlElement(name = "nonce_str", required = false)
    private String nonceStr;

    @XmlElement(name = "sign", required = false)
    private String sign;

    @XmlElement(name = "result_code", required = false)
    private String resultCode;

    @XmlElement(name = "err_code", required = false)
    private String errCode;

    @XmlElement(name = "err_code_des", required = false)
    private String errCodeDes;

    @XmlElement(name = "partner_trade_no", required = false)
    private String partnerTradeNo;

    @XmlElement(name = "amount", required = false)
    private Integer amount;

    /**
     * 以下字段在return_code 和result_code都为SUCCESS的时候有返回.
     */
    @XmlElement(name = "trade_type", required = false)
    private String tradeType;

    @XmlElement(name = "prepay_id", required = false)
    private String prepayId;

    @XmlElement(name = "code_url", required = false)
    private String codeUrl;

    @XmlElement(name = "mweb_url", required = false)
    private String mwebUrl;

    @XmlElement(name = "payment_no", required = false)
    private String paymentNo;

    @XmlElement(name = "cmms_amt", required = false)
    private Integer cmmsAmt;
}
