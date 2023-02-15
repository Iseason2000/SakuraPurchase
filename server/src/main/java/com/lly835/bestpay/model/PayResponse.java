package com.lly835.bestpay.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lly835.bestpay.enums.BestPayPlatformEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.net.URI;
import java.util.Map;

/**
 * 支付时的同步/异步返回参数
 */
@Data
@ApiModel("支付时的同步/异步返回参数")
public class PayResponse {

    /**
     * 以下参数只有微信支付会返回 (在微信付款码支付使用)
     * returnCode returnMsg resultCode errCode errCodeDes
     */
    @ApiModelProperty("返回代码,仅微信付款码")
    private String returnCode;

    @ApiModelProperty("返回信息,仅微信付款码")
    private String returnMsg;

    @ApiModelProperty("结果代码,仅微信付款码")
    private String resultCode;

    @ApiModelProperty("错误代码,仅微信付款码")
    private String errCode;

    @ApiModelProperty("错误代码描述,仅微信付款码")
    private String errCodeDes;

    @ApiModelProperty("预支付参数")
    private String prePayParams;

    @ApiModelProperty("支付连接")
    private URI payUri;

    /**
     * 以下字段仅在微信h5支付返回.
     */
    @ApiModelProperty("appID,仅微信h5")
    private String appId;

    @ApiModelProperty("时间戳,仅微信h5")
    private String timeStamp;

    @ApiModelProperty("nonceStr,仅微信h5")
    private String nonceStr;

    @ApiModelProperty("package,仅微信h5")
    @JsonProperty("package")
    private String packAge;

    @ApiModelProperty("signType,仅微信h5")
    private String signType;

    @ApiModelProperty("paySign,仅微信h5")
    private String paySign;

    /**
     * 以下字段在微信异步通知下返回.
     */
    @ApiModelProperty("订单金额,仅微信异步通知")
    private Double orderAmount;

    @ApiModelProperty("订单ID,仅微信异步通知")
    private String orderId;

    /**
     * 第三方支付的流水号
     */
    @ApiModelProperty("第三方支付的流水号")
    private String outTradeNo;

    /**
     * 以下支付是h5支付返回
     */
    @ApiModelProperty("h5支付返回")
    private String mwebUrl;

    /**
     * AliPay  pc网站支付返回的body体，html 可直接嵌入网页使用
     */
    @ApiModelProperty("AliPay  pc网站支付返回的body体，html 可直接嵌入网页使用")
    private String body;

    /**
     * 扫码付模式二用来生成二维码
     */
    @ApiModelProperty("扫码付模式二用来生成二维码")
    private String codeUrl;

    /**
     * 附加内容，发起支付时传入
     */
    @ApiModelProperty("附加内容，发起支付时传入")
    private String attach;

    @ApiModelProperty("支付平台")
    private BestPayPlatformEnum payPlatformEnum;

    @ApiModelProperty("预支付id")
    private String prepayId;

    private Map<String, String> map;
}
