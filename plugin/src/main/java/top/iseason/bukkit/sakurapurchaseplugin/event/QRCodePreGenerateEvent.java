package top.iseason.bukkit.sakurapurchaseplugin.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import top.iseason.bukkit.sakurapurchaseplugin.entity.Order;

import java.awt.image.BufferedImage;

/**
 * 二维码生成之前的事件，可以控制二维码的样式
 */
public class QRCodePreGenerateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    /**
     * 二维码连接
     */
    private final String content;
    private final Player player;
    private final Order order;
    /**
     * 二维码宽度
     */
    private int qrWidth;
    /**
     * 二维码高度
     */
    private int qrHeight;
    /**
     * 二维码中心的logo，设置为null不显示
     */
    private BufferedImage logo;
    /**
     * 二维码中心logo宽度
     */
    private int logoWidth;
    /**
     * 二维码中心logo高度
     */
    private int logoHeight;
    /**
     * 二维码颜色
     */
    private int qrColor;
    /**
     * 是否自适应图像大小至地图能够显示的大小
     */
    private boolean isResize = true;

    public QRCodePreGenerateEvent(String content, int qrWidth, int qrHeight, int logoWidth, int logoHeight, BufferedImage logo, int qrColor, Player player, Order order) {
        super(!Bukkit.isPrimaryThread());
        this.content = content;
        this.qrWidth = qrWidth;
        this.qrHeight = qrHeight;
        this.logoWidth = logoWidth;
        this.logoHeight = logoHeight;
        this.logo = logo;
        this.qrColor = qrColor;
        this.player = player;
        this.order = order;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public String getContent() {
        return content;
    }

    public int getQrWidth() {
        return qrWidth;
    }

    public void setQrWidth(int qrWidth) {
        this.qrWidth = qrWidth;
    }

    public int getQrHeight() {
        return qrHeight;
    }

    public void setQrHeight(int qrHeight) {
        this.qrHeight = qrHeight;
    }

    public int getLogoWidth() {
        return logoWidth;
    }

    public void setLogoWidth(int logoWidth) {
        this.logoWidth = logoWidth;
    }

    public int getLogoHeight() {
        return logoHeight;
    }

    public void setLogoHeight(int logoHeight) {
        this.logoHeight = logoHeight;
    }

    public BufferedImage getLogo() {
        return logo;
    }

    public void setLogo(BufferedImage logo) {
        this.logo = logo;
    }

    public int getQrColor() {
        return qrColor;
    }

    public void setQrColor(int qrColor) {
        this.qrColor = qrColor;
    }

    public boolean isResize() {
        return isResize;
    }

    public void setResize(boolean resize) {
        isResize = resize;
    }

    public Player getPlayer() {
        return player;
    }

    public Order getOrder() {
        return order;
    }
}
