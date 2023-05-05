package top.iseason.bukkit.sakurapurchaseplugin.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import top.iseason.bukkit.sakurapurchaseplugin.entity.Order;

import java.awt.image.BufferedImage;

/**
 * 支付二维码地图生成之前调用，取消不显示二维码地图
 */
public class QRMapGenerateEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    /**
     * 二维码连接
     */
    private final String url;
    private final Player player;
    private final Order order;
    /**
     * 二维码图像
     */
    private BufferedImage image;
    private boolean isCancelled = false;

    public QRMapGenerateEvent(String url, BufferedImage image, Player player, Order order) {
        super(!Bukkit.isPrimaryThread());
        this.url = url;
        this.image = image;
        this.player = player;
        this.order = order;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public String getUrl() {
        return url;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public Player getPlayer() {
        return player;
    }

    public Order getOrder() {
        return order;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }
}
