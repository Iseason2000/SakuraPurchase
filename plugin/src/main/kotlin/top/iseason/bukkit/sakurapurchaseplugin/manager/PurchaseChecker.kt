package top.iseason.bukkit.sakurapurchaseplugin.manager


import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import top.iseason.bukkit.sakurapurchaseplugin.config.Config
import top.iseason.bukkit.sakurapurchaseplugin.config.Config.formatByOrder
import top.iseason.bukkit.sakurapurchaseplugin.config.Lang
import top.iseason.bukkit.sakurapurchaseplugin.entity.Order
import top.iseason.bukkittemplate.debug.info
import top.iseason.bukkittemplate.debug.warn
import top.iseason.bukkittemplate.utils.bukkit.EntityUtils.getHeldItem
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.sendColorMessage
import top.iseason.bukkittemplate.utils.other.submit
import java.util.function.Consumer

class PurchaseChecker(

    private val player: Player,
    private val order: Order,
    val map: ItemStack,
    private val onSuccess: Consumer<Order>
) : BukkitRunnable() {
    private val timeStamp = System.currentTimeMillis()
    val oldItemStack: ItemStack? = player.getHeldItem()

    init {
        player.inventory.setItem(player.inventory.heldItemSlot, map)
        PurchaseManager.purchaseMap[player] = this
        submit {
            player.teleport(player.location.apply { pitch = 90F })
        }
    }

    /**
     * 监听订单zhuangtai
     */
    override fun run() {
        val timePast = System.currentTimeMillis() - timeStamp
        val maxWait = Config.maxTimeout * 1000
        if (timePast >= maxWait) {
            player.sendColorMessage(
                Lang.pay__timeout.formatByOrder(order)
            )
            info("&7用户 &6${player.name} &7订单 &6${order.orderId} &7已超时")
            cancelSilently()
            return
        }
        val query = PurchaseManager.query(order.orderId)
        if (query == "SUCCESS") {
            player.sendColorMessage(
                Lang.pay__sucess.formatByOrder(order)
            )
            info("&7玩家 &6${player.name} &7订单 &6${order.orderId} &a已完成支付(${order.payType.translation}), &7金额 &6${order.amount} &7商品信息: &f${order.orderName} ${order.attach}")
            cancelSilently()
            if (!PurchaseManager.saveOrder(order)) {
                warn("保存订单异常，请检查链接!")
            }
            //加入缓存
            PlayerInfoCacheManager.finish(player.uniqueId, order)
            onSuccess.accept(order)
            return
        }
        player.sendColorMessage(
            Lang.pay__waiting.formatByOrder(order).replace("{time}", ((maxWait - timePast) / 1000).toString())
        )

    }

    /**
     * 取消监听订单状态
     */
    override fun cancel() {
        cancelSilently()
        player.sendColorMessage(
            Lang.pay__cancel.formatByOrder(order)
        )
        info("&7用户 &6${player.name} &7订单 &6${order.orderId} &7已取消")
    }

    /**
     * 取消，但是不提示
     */
    private fun cancelSilently() {
        super.cancel()
        player.inventory.setItem(player.inventory.heldItemSlot, oldItemStack)
        PlayerInfoCacheManager.getPlayerInfo(player.uniqueId).currentOrder = null
        PurchaseManager.purchaseMap.remove(this.player)
    }
}