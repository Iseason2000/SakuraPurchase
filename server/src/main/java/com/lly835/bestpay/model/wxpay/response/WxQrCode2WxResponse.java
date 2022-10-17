package com.lly835.bestpay.model.wxpay.response;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * 响应微信扫码回调的返回
 * Created by steven ma
 * 2019/9/10 18:12
 */

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml")
public class WxQrCode2WxResponse {

    @XmlElement(name = "return_code")
    private String returnCode;

    @XmlElement(name = "return_msg", required = false)
    private String returnMsg;

    @XmlElement(name = "appid", required = false)
    private String appid;

    @XmlElement(name = "mch_id", required = false)
    private String mchId;

    @XmlElement(name = "nonce_str", required = false)
    private String nonceStr;

    @XmlElement(name = "prepay_id", required = false)
    private String prepayId;

    @XmlElement(name = "result_code", required = false)
    private String resultCode;

    @XmlElement(name = "err_code_des", required = false)
    private String errCodeDes;

    @XmlElement(name = "sign", required = false)
    private String sign;
}
