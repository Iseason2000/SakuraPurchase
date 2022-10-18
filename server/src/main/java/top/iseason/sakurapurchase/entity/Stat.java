package top.iseason.sakurapurchase.entity;

import lombok.Data;

/**
 * 统计
 */
@Data
public class Stat {
    private String period;
    private Double amount;
    private Integer count;
}
