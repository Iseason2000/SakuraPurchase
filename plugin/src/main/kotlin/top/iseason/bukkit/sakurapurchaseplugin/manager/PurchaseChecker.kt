package top.iseason.bukkit.sakurapurchaseplugin.manager


import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import top.iseason.bukkit.sakurapurchaseplugin.config.Config
import top.iseason.bukkit.sakurapurchaseplugin.config.Config.formatByOrder
import top.iseason.bukkit.sakurapurchaseplugin.config.Language
import top.iseason.bukkit.sakurapurchaseplugin.entity.Order
import top.iseason.bukkit.sakurapurchaseplugin.event.OrderCancelEvent
import top.iseason.bukkit.sakurapurchaseplugin.event.OrderFinishEvent
import top.iseason.bukkit.sakurapurchaseplugin.event.OrderTimeoutEvent
import top.iseason.bukkittemplate.debug.info
import top.iseason.bukkittemplate.debug.warn
import top.iseason.bukkittemplate.utils.bukkit.EntityUtils.getHeldItem
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.sendColorMessage
import top.iseason.bukkittemplate.utils.bukkit.SchedulerUtils.submit
import java.util.function.Consumer

class PurchaseChecker(
    private val player: Player,
    private val order: Order,
    val map: ItemStack,
    private val onSuccess: Consumer<Order>
) : BukkitRunnable() {
    private val timeStamp = System.currentTimeMillis()
    val oldItemStack: ItemStack? = player.getHeldItem()

    @Volatile
    private var isInnerCancelled = false

    init {
        if (map.type != Material.AIR) {
            player.inventory.setItem(player.inventory.heldItemSlot, map)
            submit {
                player.teleport(player.location.apply { pitch = 90F })
            }
        }
    }

    /**
     * 监听订单zhuangtai
     */
    override fun run() {
        if (isInnerCancelled) return
        val timePast = System.currentTimeMillis() - timeStamp
        val maxWait = Config.maxTimeout * 1000
        if (timePast >= maxWait) {
            player.sendColorMessage(
                Language.pay__timeout.formatByOrder(order)
            )
            val event = OrderTimeoutEvent(order, player)
            Bukkit.getPluginManager().callEvent(event)
            info("&7用户 &6${player.name} &7订单 &6${order.orderId} &7已超时")
            cancelSilently()
            return
        }
        val query = PurchaseManager.query(order.orderId)
        if (query == "SUCCESS") {
            val event = OrderFinishEvent(order, player)
            Bukkit.getPluginManager().callEvent(event)
            player.sendColorMessage(
                Language.pay__success.formatByOrder(order)
            )
            cancelSilently(false)
            info("&7玩家 &6${player.name} &7订单 &6${order.orderId} &a已完成支付(${order.payType.translation}), &7金额 &6${order.amount} &7命令组: &f${order.orderName} ${order.attach}")
            if (!PurchaseManager.saveOrder(order)) {
                warn("保存订单异常，请检查链接!")
            }
            //加入缓存
            PlayerInfoCacheManager.finish(player.uniqueId, order)
            onSuccess.accept(order)
            return
        }
        player.sendColorMessage(
            Language.pay__waiting.formatByOrder(order).replace("{time}", ((maxWait - timePast) / 1000).toString())
        )

    }

    /**
     * 取消监听订单状态
     */
    override fun cancel() {
        if (isInnerCancelled) return
        cancelSilently()
        player.sendColorMessage(
            Language.pay__cancel.formatByOrder(order)
        )
        val event = OrderCancelEvent(order, player)
        Bukkit.getPluginManager().callEvent(event)
        info("&7用户 &6${player.name} &7订单 &6${order.orderId} &7已取消")
    }

    /**
     * 取消，但是不提示
     */
    private fun cancelSilently(setClose: Boolean = true) {
        if (isInnerCancelled) return
        isInnerCancelled = true
        super.cancel()
        if (map.type != Material.AIR) {
            player.inventory.setItem(player.inventory.heldItemSlot, oldItemStack)
        }
        PlayerInfoCacheManager.getPlayerInfo(player.uniqueId).currentOrder = null
        PurchaseManager.purchaseMap.remove(this.player)
        if (setClose) PurchaseManager.closeOrder(order);
    }
}