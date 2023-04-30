package top.iseason.bukkit.sakurapurchaseplugin.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 玩家支付成功时的通知
 * 取消则不会运行命令
 */
public class PurchaseSuccessEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final String payType;
    private final String name;
    private final String attach;
    private boolean isCancelled = false;
    private double amount;
    private String commandGroup;

    public PurchaseSuccessEvent(
            Player player,
            double amount,
            String payType,
            String name,
            String attach,
            String commandGroup
    ) {
        super(!Bukkit.isPrimaryThread());
        this.player = player;
        this.amount = amount;
        this.payType = payType;
        this.name = name;
        this.attach = attach;
        this.commandGroup = commandGroup;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }

    public Player getPlayer() {
        return player;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPayType() {
        return payType;
    }

    public String getCommandGroup() {
        return commandGroup;
    }

    public void setCommandGroup(String commandGroup) {
        this.commandGroup = commandGroup;
    }

    public String getName() {
        return name;
    }

    public String getAttach() {
        return attach;
    }
}
