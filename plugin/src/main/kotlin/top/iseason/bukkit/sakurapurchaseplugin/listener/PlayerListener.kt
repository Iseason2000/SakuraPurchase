package top.iseason.bukkit.sakurapurchaseplugin.listener

import org.bukkit.Material
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
import top.iseason.bukkit.sakurapurchaseplugin.config.Language
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
        PurchaseManager.closeOrder(event.player)
        PlayerInfoCacheManager.remove(event.player.uniqueId)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val purchaseChecker = PurchaseManager.purchaseMap[event.entity] ?: return
        if (!event.keepInventory && purchaseChecker.map.type != Material.AIR) {
            event.drops.removeIf { it == purchaseChecker.map }
            event.drops.add(purchaseChecker.oldItemStack)
        }
        PurchaseManager.closeOrder(event.entity)
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (!PurchaseManager.hasOrder(event.player)) return
        if (Config.cancelAction == "SHIFT_F") event.isCancelled = true
        else if (event.player.eyeLocation.pitch < 0) {
            PurchaseManager.closeOrder(event.player)
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerDamage(event: EntityDamageEvent) {
        val player = event.entity as? Player ?: return
        if (PurchaseManager.hasOrder(player) && Config.cancelAction == "SHIFT_F") event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (PurchaseManager.hasOrder(event.player)) event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        if (PurchaseManager.hasOrder(event.player)) event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerInvClick(event: InventoryClickEvent) {
        if (event.clickedInventory !is PlayerInventory) return
        if (PurchaseManager.hasOrder(event.whoClicked as Player)) event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerInvOpen(event: InventoryOpenEvent) {
        if (PurchaseManager.hasOrder(event.player as Player)) event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerDrop(event: PlayerDropItemEvent) {
        if (PurchaseManager.hasOrder(event.player)) event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerDrop(event: PlayerItemHeldEvent) {
        if (PurchaseManager.hasOrder(event.player)) event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerCommand(event: PlayerCommandPreprocessEvent) {
        if (PurchaseManager.hasOrder(event.player)) {
            event.player.sendColorMessage(Language.pay__command_block)
            event.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        if (!PurchaseManager.hasOrder(event.player)) return
        for (key in Config.cancelWorld) {
            if (event.message.contains(key)) {
                PurchaseManager.closeOrder(event.player)
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