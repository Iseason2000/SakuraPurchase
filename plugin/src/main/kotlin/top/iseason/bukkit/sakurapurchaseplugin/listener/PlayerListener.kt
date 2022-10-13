package top.iseason.bukkit.sakurapurchaseplugin.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.PlayerInventory
import top.iseason.bukkit.sakurapurchaseplugin.config.Lang
import top.iseason.bukkit.sakurapurchaseplugin.service.PurchaseService
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.sendColorMessage

object PlayerListener : Listener {


    @EventHandler
    fun onPlayerSwap(event: PlayerSwapHandItemsEvent) {
        if (!PurchaseService.purchaseMap.containsKey(event.player)) return
        // shift+F 取消支付
        if (event.player.isSneaking) {
            PurchaseService.purchaseMap[event.player]!!.cancel()
            return
        }
        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        PurchaseService.purchaseMap[event.player]?.cancel()
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerQuit(event: PlayerDeathEvent) {
        val purchaseChecker = PurchaseService.purchaseMap[event.entity] ?: return
        if (!event.keepInventory) {
            event.drops.removeIf { it == purchaseChecker.map }
            event.drops.add(purchaseChecker.oldItemStack)
        }
        PurchaseService.purchaseMap[event.entity]?.cancel()
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (PurchaseService.purchaseMap.containsKey(event.player)) event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerInvClick(event: InventoryClickEvent) {
        if (event.clickedInventory !is PlayerInventory) return
        if (PurchaseService.purchaseMap.containsKey(event.whoClicked)) event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerInvOpen(event: InventoryOpenEvent) {
        if (PurchaseService.purchaseMap.containsKey(event.player)) event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerDrop(event: PlayerDropItemEvent) {
        if (PurchaseService.purchaseMap.containsKey(event.player)) event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerDrop(event: PlayerItemHeldEvent) {
        if (PurchaseService.purchaseMap.containsKey(event.player)) event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerCommand(event: PlayerCommandPreprocessEvent) {
        if (PurchaseService.purchaseMap.containsKey(event.player)) {
            event.player.sendColorMessage(Lang.pay__command_block)
            event.isCancelled = true
        }
    }

}