package top.iseason.bukkit.sakurapurchaseplugin.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import top.iseason.bukkit.sakurapurchaseplugin.config.Config
import top.iseason.bukkit.sakurapurchaseplugin.manager.PurchaseManager

/**
 * 单独拿出来是因为低版本没有这个事件
 */
object SwapListener : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerSwap(event: PlayerSwapHandItemsEvent) {
        if (!PurchaseManager.purchaseMap.containsKey(event.player)) return
        // shift+F 取消支付
        if (event.player.isSneaking && Config.cancelAction == "SHIFT_F") {
            PurchaseManager.closeOrder(event.player)
        }
        event.isCancelled = true
    }
}