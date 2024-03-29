package com.lly835.bestpay.model.wxpay.response;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * 微信退款返回参数
 * Created by 廖师兄
 * 2017-07-02 13:33
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml") //name:要解析的xml数据的头部
public class WxRefundResponse {

    @XmlElement(name = "return_code", required = true)
    private String returnCode;

    @XmlElement(name = "return_msg")
    private String returnMsg;

    /**
     * 以下字段在return_code为SUCCESS的时候有返回.
     */
    @XmlElement(name = "result_code")
    private String resultCode;

    @XmlElement(name = "err_code")
    private String errCode;

    @XmlElement(name = "err_code_des")
    private String errCodeDes;

    @XmlElement(name = "appid")
    private String appid;

    @XmlElement(name = "mch_id")
    private String mchId;

    @XmlElement(name = "nonce_str")
    private String nonceStr;

    @XmlElement(name = "sign")
    private String sign;

    @XmlElement(name = "transaction_id")
    private String transactionId;

    @XmlElement(name = "out_trade_no")
    private String outTradeNo;

    @XmlElement(name = "out_refund_no")
    private String outRefundNo;

    @XmlElement(name = "refund_id")
    private String refundId;

    @XmlElement(name = "refund_fee")
    private Integer refundFee;

    @XmlElement(name = "settlement_refund_fee")
    private Integer settlementRefundFee;

    @XmlElement(name = "total_fee")
    private Integer totalFee;

    @XmlElement(name = "settlement_total_fee")
    private Integer settlementTotalFee;

    @XmlElement(name = "fee_type")
    private String feeType;

    @XmlElement(name = "cash_fee")
    private Integer cashFee;

    @XmlElement(name = "cash_fee_type")
    private String cashFeeType;

    @XmlElement(name = "cash_refund_fee")
    private Integer cashRefundFee;

    @XmlElement(name = "coupon_refund_fee")
    private Integer couponRefundFee;

    @XmlElement(name = "coupon_refund_count")
    private Integer couponRefundCount;
}
