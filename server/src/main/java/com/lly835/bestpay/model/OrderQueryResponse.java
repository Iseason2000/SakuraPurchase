package com.lly835.bestpay.model;

import com.lly835.bestpay.enums.OrderStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * 订单查询结果
 * Created by 廖师兄
 * 2018-06-04 16:52
 */
@Data
@Builder
@ApiModel("订单查询结果")
public class OrderQueryResponse {

    /**
     * 订单状态
     */
    @ApiModelProperty("订单状态")
    private OrderStatusEnum orderStatusEnum;

    /**
     * 第三方支付的流水号
     */
    @ApiModelProperty("第三方支付的流水号")
    private String outTradeNo;

    /**
     * 附加内容，发起支付时传入
     */
    @ApiModelProperty("附加内容，发起支付时传入")
    private String attach;

    /**
     * 错误原因
     */
    @ApiModelProperty("错误原因")
    private String resultMsg;

    /**
     * 订单号
     */
    @ApiModelProperty("订单号")
    private String orderId;

    /**
     * 支付完成时间
     */
    @ApiModelProperty("支付完成时间")
    private String finishTime;
}
