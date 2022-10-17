package com.lly835.bestpay.model.wxpay.request;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * 退款请求参数
 * Created by 廖师兄
 * 2017-07-02 01:09
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml")
public class WxPayRefundRequest {

    @XmlElement(name = "appid")
    private String appid;

    @XmlElement(name = "mch_id")
    private String mchId;

    @XmlElement(name = "nonce_str")
    private String nonceStr;

    @XmlElement(name = "sign")
    private String sign;

    @XmlElement(name = "sign_type", required = false)
    private String signType;

    @XmlElement(name = "transaction_id", required = false)
    private String transactionId;

    @XmlElement(name = "out_trade_no")
    private String outTradeNo;

    @XmlElement(name = "out_refund_no")
    private String outRefundNo;

    @XmlElement(name = "total_fee")
    private Integer totalFee;

    @XmlElement(name = "refund_fee")
    private Integer refundFee;

    @XmlElement(name = "refund_fee_type", required = false)
    private String refundFeeType;

    @XmlElement(name = "refund_desc", required = false)
    private String refundDesc;

    @XmlElement(name = "refund_account", required = false)
    private String refundAccount;
}
