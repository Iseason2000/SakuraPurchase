package top.iseason.bukkit.sakurapurchaseplugin.listener

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.PlayerInventory
import top.iseason.bukkit.sakurapurchaseplugin.config.Lang
import top.iseason.bukkit.sakurapurchaseplugin.manager.PlayerInfoCacheManager
import top.iseason.bukkit.sakurapurchaseplugin.manager.PurchaseManager
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.sendColorMessage

object PlayerListener : Listener {


    @EventHandler
    fun onPlayerSwap(event: PlayerSwapHandItemsEvent) {
        if (!PurchaseManager.purchaseMap.containsKey(event.player)) return
        // shift+F 取消支付
        if (event.player.isSneaking) {
            PurchaseManager.purchaseMap[event.player]!!.cancel()
            return
        }
        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        PurchaseManager.purchaseMap[event.player]?.cancel()
        PlayerInfoCacheManager.remove(event.player.uniqueId)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerQuit(event: PlayerDeathEvent) {
        val purchaseChecker = PurchaseManager.purchaseMap[event.entity] ?: return
        if (!event.keepInventory) {
            event.drops.removeIf { it == purchaseChecker.map }
            event.drops.add(purchaseChecker.oldItemStack)
        }
        PurchaseManager.purchaseMap[event.entity]?.cancel()
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (PurchaseManager.purchaseMap.containsKey(event.player)) event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerDamage(event: EntityDamageEvent) {
        val player = event.entity as? Player ?: return
        if (PurchaseManager.purchaseMap.containsKey(player)) event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerInvClick(event: InventoryClickEvent) {
        if (event.clickedInventory !is PlayerInventory) return
        if (PurchaseManager.purchaseMap.containsKey(event.whoClicked)) event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerInvOpen(event: InventoryOpenEvent) {
        if (PurchaseManager.purchaseMap.containsKey(event.player)) event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerDrop(event: PlayerDropItemEvent) {
        if (PurchaseManager.purchaseMap.containsKey(event.player)) event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerDrop(event: PlayerItemHeldEvent) {
        if (PurchaseManager.purchaseMap.containsKey(event.player)) event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerCommand(event: PlayerCommandPreprocessEvent) {
        if (PurchaseManager.purchaseMap.containsKey(event.player)) {
            event.player.sendColorMessage(Lang.pay__command_block)
            event.isCancelled = true
        }
    }

}