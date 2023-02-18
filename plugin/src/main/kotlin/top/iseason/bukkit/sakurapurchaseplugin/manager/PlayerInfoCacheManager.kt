package top.iseason.bukkit.sakurapurchaseplugin.manager

import top.iseason.bukkit.sakurapurchaseplugin.config.Config
import top.iseason.bukkit.sakurapurchaseplugin.config.OrderCache
import top.iseason.bukkit.sakurapurchaseplugin.entity.Order
import top.iseason.bukkit.sakurapurchaseplugin.entity.PlayerInfo
import top.iseason.bukkit.sakurapurchaseplugin.util.getValue
import top.iseason.bukkit.sakurapurchaseplugin.util.lazyMutable
import top.iseason.bukkit.sakurapurchaseplugin.util.setValue
import java.util.*
import java.util.concurrent.ConcurrentHashMap

internal object PlayerInfoCacheManager {

    private val playerCache = ConcurrentHashMap<UUID, PlayerInfo>()
    private var isCached = false

    /**
     * 全服充值总金额 懒加载
     */
    var totalAmount by lazyMutable {
        modifyCache = 0.0
        isCached = true
        requestTotalAmount()
    }

    /**
     * 先将修改提交至本地缓存，当需要获取总额时才发送网络请求,并将本地修改同步到网络值中
     * 初始化之后对当前值的修改会映射到totalAmount的缓存中
     */
    var modifyCache = 0.0
        @Synchronized set(filed) {
            if (isCached) totalAmount += filed
            else field = filed
        }

    fun getPlayerInfo(uuid: UUID): PlayerInfo {
        return playerCache.computeIfAbsent(uuid) { PlayerInfo(uuid) }
    }

    /**
     * 获取玩家总消费金额
     */
    fun requestPlayerTotalAmount(uuid: UUID): Double {
        if (!Connection.isConnected) return 0.0
        val httpGet = Connection.httpGet("${Config.userTotalUrl}/$uuid")
        return if (httpGet.isSuccess())
            httpGet.data?.asDouble ?: 0.0
        else 0.0
    }

    /**
     * 获取玩家支付记录
     */
    fun requestPlayerOrders(uuid: UUID, offset: Int = 0, amount: Int = 0): MutableList<Order> {
        if (!Connection.isConnected) return mutableListOf()
        val url =
            if (amount != 0) {
                "${Config.userAllUrl}/$uuid?offset=$offset&amount=$amount"
            } else "${Config.userAllUrl}/$uuid"
        val httpGet = Connection.httpGet(url)
        val mutableListOf = mutableListOf<Order>()
        if (httpGet.isSuccess()) {
            httpGet.data?.asJsonArray?.forEach {
                val order = Order.from(uuid, it.asJsonObject) ?: return@forEach
                mutableListOf.add(order)
            }
        }
        return mutableListOf
    }

    fun requestTotalAmount(): Double {
        if (!Connection.isConnected) return 0.0
        val httpGet = Connection.httpGet(Config.totalAmountUrl)
        return if (httpGet.isSuccess())
            httpGet.data?.asDouble ?: 0.0
        else 0.0
    }

    /**
     * 删除玩家缓存
     */
    fun remove(uuid: UUID) {
        playerCache.remove(uuid)
    }

    /**
     * 完成订单时设置对应的缓存等信息
     */
    fun finish(uuid: UUID, order: Order) {
        val playerInfo = getPlayerInfo(uuid)
        //初始化过才加入缓存
        if (playerInfo.isInitOrders)
            playerInfo.orders.add(order)
        playerInfo.currentOrder = null
        OrderCache.orderCache.remove(uuid)
        OrderCache.groupCache.remove(uuid)
        playerInfo.lastOrder = order
        modifyCache += order.amount
    }
}