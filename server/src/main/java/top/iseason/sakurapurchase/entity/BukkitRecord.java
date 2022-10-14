package top.iseason.sakurapurchase.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 由 bukkit 端 发起的有效支付
 */
@Data
@AllArgsConstructor
public class BukkitRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String uuid;
    private Long orderId;
}
