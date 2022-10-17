package com.lly835.bestpay.model.wxpay.request;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by steven ma
 * 2019-03-15 11:49
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml")
public class WxDownloadBillRequest {

    //公众账号ID
    @XmlElement(name = "appid")
    private String appid;

    //商户号
    @XmlElement(name = "mch_id")
    private String mchId;

    //随机字符串
    @XmlElement(name = "nonce_str")
    private String nonceStr;

    //签名
    @XmlElement(name = "sign")
    private String sign;

    //对账单日期
    @XmlElement(name = "bill_date")
    private String billDate;

    //账单类型
    @XmlElement(name = "bill_type", required = false)
    private String billType = "ALL";

//    //压缩账单
//    @XmlElement(name = "tar_type", required = false)
//    private String tarType = "GZIP";

}
