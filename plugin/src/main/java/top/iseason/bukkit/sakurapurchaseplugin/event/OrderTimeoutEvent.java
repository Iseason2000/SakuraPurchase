package top.iseason.bukkit.sakurapurchaseplugin.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import top.iseason.bukkit.sakurapurchaseplugin.entity.Order;

/**
 * 订单由于超时未被支付自动关闭时触发
 */
public class OrderTimeoutEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Order order;
    private final Player player;

    public OrderTimeoutEvent(Order order, Player player) {
        super(!Bukkit.isPrimaryThread());
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

    public Player getPlayer() {
        return player;
    }

}
