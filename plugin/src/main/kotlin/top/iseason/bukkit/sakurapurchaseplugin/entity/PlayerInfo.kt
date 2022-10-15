package top.iseason.bukkit.sakurapurchaseplugin.entity

import top.iseason.bukkit.sakurapurchaseplugin.manager.PlayerInfoCacheManager
import top.iseason.bukkit.sakurapurchaseplugin.util.getValue
import top.iseason.bukkit.sakurapurchaseplugin.util.lazyMutable
import top.iseason.bukkit.sakurapurchaseplugin.util.setValue
import java.util.*

data class PlayerInfo(
    val uuid: UUID
) {
    /**
     * 全部订单
     */
    val orders: MutableList<Order> by lazyMutable {
        PlayerInfoCacheManager.requestPlayerOrders(uuid)
    }


    /**
     * 总消费
     */
    var totalAmount by lazyMutable { PlayerInfoCacheManager.requestPlayerTotalAmount(uuid) }

    /**
     * 上个订单
     */
    var lastOrder by lazyMutable { PlayerInfoCacheManager.requestPlayerOrders(uuid, 0, 1).firstOrNull() }

    /**
     * 当前正在进行的订单
     */
    var currentOrder: Order? = null

    /**
     * 获取最新的 amount 个记录
     */
    fun getLastOrders(offset: Int, amount: Int): List<Order> {
        val mutableListOf = mutableListOf<Order>()
        for (index in offset..offset + amount) {
            val orNull = orders.getOrNull(index) ?: continue
            mutableListOf.add(orNull)
        }
        return mutableListOf
    }

    /**
     * 获取第 index 个记录
     * @param desc 是否倒序
     */
    fun getOrder(index: Int, desc: Boolean = false) =
        if (!desc) orders.getOrNull(index) else orders.getOrNull(orders.size - 1 - index)

}