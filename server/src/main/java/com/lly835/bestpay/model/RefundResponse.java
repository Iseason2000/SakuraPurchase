package com.lly835.bestpay.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 退款返回的参数
 * Created by 廖师兄
 * 2017-07-08 23:40
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ApiModel("退款返回的参数")
public class RefundResponse {

    /**
     * 订单号.
     */
    @ApiModelProperty("订单号")
    private String orderId;

    /**
     * 订单金额.
     */
    @ApiModelProperty("订单金额")
    private Double orderAmount;

    /**
     * 第三方支付流水号.
     */
    @ApiModelProperty("第三方支付流水号")
    private String outTradeNo;

    /**
     * 退款号.
     */
    @ApiModelProperty("退款号")
    private String refundId;

    /**
     * 第三方退款流水号.
     */
    @ApiModelProperty("第三方退款流水号")
    private String outRefundNo;


}
