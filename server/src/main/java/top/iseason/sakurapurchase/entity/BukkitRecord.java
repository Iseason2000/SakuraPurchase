package top.iseason.sakurapurchase.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 由 bukkit 端 发起的有效支付
 */
@Data
@AllArgsConstructor
@ApiModel("bukkit支付记录")
public class BukkitRecord implements Serializable {

    @ApiModelProperty("ID")
    @TableId(type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("用户uuid")
    private String uuid;

    @ApiModelProperty("订单id")
    private Long orderId;
}
