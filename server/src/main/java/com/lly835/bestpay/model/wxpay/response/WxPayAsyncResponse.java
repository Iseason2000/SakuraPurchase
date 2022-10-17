package com.lly835.bestpay.model.wxpay.response;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * 异步返回参数
 * Created by 廖师兄
 * 2017-07-02 20:55
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml")
public class WxPayAsyncResponse {

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

    @XmlElement(name = "openid", required = false)
    private String openid;

    @XmlElement(name = "is_subscribe", required = false)
    private String isSubscribe;

    @XmlElement(name = "trade_type", required = false)
    private String tradeType;

    @XmlElement(name = "bank_type", required = false)
    private String bankType;

    @XmlElement(name = "total_fee", required = false)
    private Integer totalFee;

    @XmlElement(name = "fee_type", required = false)
    private String feeType;

    @XmlElement(name = "cash_fee", required = false)
    private String cashFee;

    @XmlElement(name = "cash_fee_type", required = false)
    private String cashFeeType;

    @XmlElement(name = "coupon_fee", required = false)
    private String couponFee;

    @XmlElement(name = "coupon_count", required = false)
    private String couponCount;

    @XmlElement(name = "transaction_id", required = false)
    private String transactionId;

    @XmlElement(name = "out_trade_no", required = false)
    private String outTradeNo;

    @XmlElement(name = "attach", required = false)
    private String attach;

    @XmlElement(name = "time_end", required = false)
    private String timeEnd;

    @XmlElement(name = "mweb_url", required = false)
    private String mwebUrl;

    //支付优惠时多返回字段
    @XmlElement(name = "settlement_total_fee", required = false)
    private Integer settlementTotalFee;

    @XmlElement(name = "coupon_type", required = false)
    private String couponType;
}