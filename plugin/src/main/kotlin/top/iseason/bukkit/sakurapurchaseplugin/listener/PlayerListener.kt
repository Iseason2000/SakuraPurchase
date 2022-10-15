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
import top.iseason.bukkit.sakurapurchaseplugin.config.Config
import top.iseason.bukkit.sakurapurchaseplugin.config.Lang
import top.iseason.bukkit.sakurapurchaseplugin.config.OrderCache
import top.iseason.bukkit.sakurapurchaseplugin.manager.PlayerInfoCacheManager
import top.iseason.bukkit.sakurapurchaseplugin.manager.PurchaseManager
import top.iseason.bukkittemplate.debug.info
import top.iseason.bukkittemplate.debug.warn
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.sendColorMessage
import top.iseason.bukkittemplate.utils.other.runAsync

object PlayerListener : Listener {


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

    @EventHandler(ignoreCancelled = true)
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        for (key in Config.cancelWorld) {
            if (event.message.contains(key)) {
                PurchaseManager.purchaseMap[event.player]!!.cancel()
                event.isCancelled = true
                return
            }
        }
    }

    /**
     * 玩家登录时检查是否有已支付但未完成的订单，如果有就完成他
     */
    fun doOnLogin(player: Player) {
        val uuid = player.uniqueId
        val order = OrderCache.orderCache[uuid] ?: return
        val group = OrderCache.groupCache[uuid] ?: return
        runAsync {
            val status = PurchaseManager.query(order.orderId)
            if (status != "SUCCESS") {
                OrderCache.orderCache.remove(uuid)
                OrderCache.groupCache.remove(uuid)
                return@runAsync
            }
            val commands = Config.commandGroup[group]
            if (commands != null) {
                info("&7玩家 &6${player.name} &7具有已支付但未完成的订单,运行命令组: &6$group")
            } else {
                info("&7玩家 &6${player.name} &7具有已支付但未完成的订单,但命令组: &6$group &7不存在")
                info(order.toString())
                return@runAsync
            }
            info(order.toString())
            Config.performCommands(player, order.amount, commands)
            PlayerInfoCacheManager.finish(uuid, order)
            if (!PurchaseManager.saveOrder(order)) {
                warn("保存订单异常，请检查链接!")
            }
        }
    }

}