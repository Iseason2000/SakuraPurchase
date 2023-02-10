package top.iseason.sakurapurchase.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 统计
 */
@Data
@ApiModel("统计")
public class Stat implements Serializable {
    @ApiModelProperty("周期")
    private String period;
    @ApiModelProperty("金额")
    private Double amount;
    @ApiModelProperty("订单数")
    private Integer count;
}
