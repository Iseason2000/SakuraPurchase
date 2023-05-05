package top.iseason.bukkit.sakurapurchaseplugin.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import top.iseason.bukkit.sakurapurchaseplugin.entity.Order;

/**
 * 订单被用户取消或者用户退出游戏取消订单触发
 */
public class OrderCancelEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Order order;
    private final Player player;

    public OrderCancelEvent(Order order, Player player) {
        this.order = order;
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public Order getOrder() {
        return order;
    }
}
