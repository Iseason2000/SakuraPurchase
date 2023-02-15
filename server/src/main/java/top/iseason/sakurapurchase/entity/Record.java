package top.iseason.sakurapurchase.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.enums.OrderStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import top.iseason.sakurapurchase.utils.DataUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Data
@Builder
@ApiModel("支付记录")
public class Record implements Serializable {
    /**
     * 订单ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty("订单ID")
    private Long orderId;
    /**
     * 平台0为支付宝 1为微信
     */
    @ApiModelProperty("订单类型,0为支付宝 1为微信")
    private Integer payType;
    /**
     * 订单状态
     */
    @ApiModelProperty("订单状态")
    private String status;

    /**
     * 订单商品名称
     */
    @ApiModelProperty("订单商品名称")
    private String orderName;

    /**
     * 订单金额
     */
    @ApiModelProperty("订单金额")
    private Double orderAmount;
    /**
     * 订单流水号
     */
    @ApiModelProperty("订单流水号")
    private String outTradeNo;
    /**
     * 附加信息
     */
    @ApiModelProperty("附加信息")
    private String attach;
    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建日期")
    private Date createTime;

    public OrderStatusEnum getOrderStatus() {
        return OrderStatusEnum.findByName(status);
    }

    public String getStatusDesc() {
        return getOrderStatus().getDesc();
    }

    public BestPayPlatformEnum getPlatformEnum() {
        return getPayTypeEnum().getPlatform();
    }

    public BestPayTypeEnum getPayTypeEnum() {
        return BestPayTypeEnum.values()[payType];
    }

    public String getPlatformName() {
        return getPlatformEnum().getName();
    }

    public String getFormatTime() {
        return DataUtils.formatDate(createTime);
    }

    @Override
    public int hashCode() {
        return orderId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Record)) return false;
        return Objects.equals(orderId, ((Record) obj).orderId);
    }
}
