package com.lly835.bestpay.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 退款返回的参数
 * Created by 廖师兄
 * 2017-07-08 23:40
 */
@Data
@ApiModel("退款返回的参数")
public class CloseResponse {

    /**
     * 订单号.
     */
    @ApiModelProperty("订单号")
    private String orderId;

    /**
     * 第三方支付流水号.
     */
    @ApiModelProperty("第三方支付流水号")
    private String outTradeNo;
}
