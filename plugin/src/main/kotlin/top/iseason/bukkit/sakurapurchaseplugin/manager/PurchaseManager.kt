package top.iseason.bukkit.sakurapurchaseplugin.manager

import com.google.gson.Gson
import okhttp3.Response
import org.bukkit.entity.Player
import top.iseason.bukkit.sakurapurchaseplugin.SakuraPurchasePlugin
import top.iseason.bukkit.sakurapurchaseplugin.config.Config
import top.iseason.bukkit.sakurapurchaseplugin.config.Config.formatByOrder
import top.iseason.bukkit.sakurapurchaseplugin.config.Lang
import top.iseason.bukkit.sakurapurchaseplugin.config.OrderCache
import top.iseason.bukkit.sakurapurchaseplugin.entity.Order
import top.iseason.bukkit.sakurapurchaseplugin.util.MapUtil
import top.iseason.bukkittemplate.debug.info
import top.iseason.bukkittemplate.debug.warn
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.sendColorMessage
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer

object PurchaseManager {
    /**
     * 正在支付的玩家
     */
    val purchaseMap = ConcurrentHashMap<Player, PurchaseChecker>()

    /**
     * 为玩家发起支付支付,并启动查询
     */
    fun purchase(
        player: Player,
        amount: Double,
        payType: PayType,
        orderName: String,
        attach: String = "",
        group: String,
        /**
         * 回调，提供 amount
         */
        onSuccess: Consumer<Order>
    ) {
        val httpPost = Connection.httpPost(Config.purchaseUrl, buildMap {
            put("type", payType.type)
            put("name", orderName)
            put("amount", amount.toString())
            put("attach", attach)
            put("_csrf", Connection.token) //防止跨域攻击
        })
        if (httpPost.isSuccess()) {
            val json = httpPost.data!!.asJsonObject
            val qrCode = json["codeUrl"].asString
            val orderID = json["orderId"].asString
            val order = Order(player.uniqueId, orderID, orderName, amount, payType, attach, Date())
            info("&7用户 &6${player.name} &7发起 &a${payType.translation} &7支付,金额: &6$amount &7订单号: &6$orderID")
            PlayerInfoCacheManager.getPlayerInfo(player.uniqueId).currentOrder = order
            OrderCache.orderCache[player.uniqueId] = order
            OrderCache.groupCache[player.uniqueId] = group
            player.sendColorMessage(
                Lang.pay__start.formatByOrder(order)
            )
            val qrMap = MapUtil.generateQRMap(qrCode) ?: return
            //默认 5秒检查一次
            val purchaseChecker = PurchaseChecker(
                player,
                order,
                qrMap,
                onSuccess
            )
            purchaseMap[player] = purchaseChecker
            purchaseChecker.runTaskTimerAsynchronously(
                SakuraPurchasePlugin.javaPlugin,
                Config.queryPeriod,
                Config.queryPeriod
            )
        } else {
            warn("发起支付失败: ${httpPost.state} ${httpPost.message}")
        }
    }

    /**
     * 查询订单状态
     */
    fun query(orderId: String): String {
        val status = "UNKNOWN"
        if (!Connection.isConnected) return status
        val httpGet = Connection.httpGet("${Config.queryUrl}/$orderId")
        if (httpGet.isSuccess()) {
            val asJsonObject = httpGet.data!!.asJsonObject
            return asJsonObject["orderStatusEnum"].asString
        }
        return status
    }

    /**
     * 支付成功时发送保存请求
     */
    fun saveOrder(order: Order): Boolean {
        if (!Connection.isConnected) return false
        val httpPost = Connection.httpPost(Config.saveUrl, buildMap {
            put("uuid", order.uuid.toString())
            put("orderId", order.orderId)
            put("_csrf", Connection.token) //防止跨域攻击
        })
        return httpPost.isSuccess()
    }

    fun closeOrder(order: Order): Boolean {
        if (!Connection.isConnected) return false
        val httpPost = Connection.httpPost(Config.closeUrl, buildMap {
            put("orderId", order.orderId)
            put("_csrf", Connection.token) //防止跨域攻击
        })
        return httpPost.isSuccess()
    }

    fun refundOrder(orderId: String): Boolean {
        if (!Connection.isConnected) return false
        val httpPost = Connection.httpPost(Config.refundUrl, buildMap {
            put("orderId", orderId)
            put("_csrf", Connection.token) //防止跨域攻击
        })
        return httpPost.isSuccess()
    }

    enum class PayType(
        val type: String,
        val translation: String
    ) {
        //阿里支付
        ALIPAY("ALIPAY_QRCODE", "支付宝"),

        //微信支付
        WXPAY("WXPAY_NATIVE", "微信");
    }

    fun Response.toStringMap() = Gson().fromJson(body!!.string(), Map::class.java)

}