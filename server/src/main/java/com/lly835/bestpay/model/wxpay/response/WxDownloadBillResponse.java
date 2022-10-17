package com.lly835.bestpay.model.wxpay.response;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * 下载对账文件返回-只有发生错误的时候才会返回
 * Created by steven ma
 * 2019/3/20 16:48
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xml")
public class WxDownloadBillResponse {

    @XmlElement(name = "return_code")
    private String returnCode;

    @XmlElement(name = "return_msg", required = false)
    private String returnMsg;

    @XmlElement(name = "error_code")
    private String errorCode;
}
