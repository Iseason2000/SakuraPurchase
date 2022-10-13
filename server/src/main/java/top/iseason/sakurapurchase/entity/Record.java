package top.iseason.sakurapurchase.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.enums.OrderStatusEnum;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Builder
public class Record {
    /**
     * 订单ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long orderId;
    /**
     * 平台0为支付宝 1为微信
     */
    private Integer platform;
    /**
     * 订单状态
     */
    private String status;
    /**
     * 订单金额
     */
    private Double orderAmount;
    /**
     * 订单流水号
     */
    private String outTradeNo;
    /**
     * 附加信息
     */
    private String attach;
    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public OrderStatusEnum getOrderStatus() {
        return OrderStatusEnum.findByName(status);
    }

    public BestPayPlatformEnum getPlatformEnum() {
        return BestPayPlatformEnum.values()[platform];
    }
}
