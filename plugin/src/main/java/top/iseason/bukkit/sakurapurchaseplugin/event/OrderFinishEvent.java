package top.iseason.bukkit.sakurapurchaseplugin.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import top.iseason.bukkit.sakurapurchaseplugin.entity.Order;

/**
 * 订单正常完成事件，发生在PurchaseSuccessEvent之前，用户支付完毕被插件检测到时
 */
public class OrderFinishEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Order order;
    private final Player player;

    public OrderFinishEvent(Order order, Player player) {
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
