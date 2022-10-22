package com.lly835.bestpay.model.wxpay.response;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml")
public class WxCloseResponse {

    @XmlElement(name = "return_code", required = true)
    private String returnCode;

    @XmlElement(name = "return_msg", required = true)
    private String returnMsg;

    /**
     * 以下字段在return_code为SUCCESS的时候有返回.
     */

    @XmlElement(name = "appid")
    private String appid;

    @XmlElement(name = "mch_id")
    private String mchId;

    @XmlElement(name = "nonce_str")
    private String nonceStr;

    @XmlElement(name = "sign")
    private String sign;

    @XmlElement(name = "result_code")
    private String resultCode;

    @XmlElement(name = "result_msg")
    private String resultMsg;

    @XmlElement(name = "err_code")
    private String errCode;

    @XmlElement(name = "err_code_des")
    private String errCodeDes;

}
