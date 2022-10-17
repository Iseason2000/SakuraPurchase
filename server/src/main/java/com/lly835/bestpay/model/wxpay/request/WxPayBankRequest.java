package com.lly835.bestpay.model.wxpay.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Created by 廖师兄
 * 2017-07-02 13:42
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml")
public class WxPayBankRequest {

    @XmlElement(name = "mch_id")
    private String mchId;

    @XmlElement(name = "nonce_str")
    private String nonceStr;

    @XmlElement(name = "sign")
    private String sign;

    /**
     * 商户企业付款单号
     */
    @XmlElement(name = "partner_trade_no")
    private String partnerTradeNo;

    /**
     * 收款方银行卡号
     */
    @XmlElement(name = "enc_bank_no")
    private String encBankNo;

    /**
     * 收款方用户名
     */
    @XmlElement(name = "enc_true_name")
    private String encTrueName;

    /**
     * 收款方开户行
     * https://pay.weixin.qq.com/wiki/doc/api/tools/mch_pay.php?chapter=24_4&index=5
     */
    @XmlElement(name = "bank_code")
    private String bankCode;

    /**
     * 付款金额
     */
    @XmlElement(name = "amount")
    private Integer amount;

    /**
     * 付款说明
     */
    @XmlElement(name = "desc")
    private String desc;
}
