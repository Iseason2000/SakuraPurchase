package top.iseason.bukkit.sakurapurchaseplugin.manager

import com.google.gson.Gson
import okhttp3.FormBody
import okhttp3.Request
import okhttp3.Response
import org.bukkit.entity.Player
import top.iseason.bukkit.sakurapurchaseplugin.SakuraPurchasePlugin
import top.iseason.bukkit.sakurapurchaseplugin.config.Config
import top.iseason.bukkit.sakurapurchaseplugin.config.Config.formatByOrder
import top.iseason.bukkit.sakurapurchaseplugin.config.Lang
import top.iseason.bukkit.sakurapurchaseplugin.config.OrderCache
import top.iseason.bukkit.sakurapurchaseplugin.entity.Order
import top.iseason.bukkit.sakurapurchaseplugin.manager.ConnectionManager.httpClient
import top.iseason.bukkit.sakurapurchaseplugin.util.MapUtil
import top.iseason.bukkittemplate.debug.SimpleLogger
import top.iseason.bukkittemplate.debug.info
import top.iseason.bukkittemplate.debug.warn
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.sendColorMessage
import java.util.*
import java.util.function.Consumer

object PurchaseManager {
    val purchaseMap = mutableMapOf<Player, PurchaseChecker>()

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

        val body = FormBody.Builder()
            .add("type", payType.type)
            .add("name", orderName)
            .add("amount", amount.toString())
            .add("attach", attach)
            .add("_csrf", ConnectionManager.token) //防止跨域攻击
            .build()
        val request = Request.Builder().url(Config.purchaseUrl).post(body).build()
        kotlin.runCatching {
            httpClient.newCall(request).execute().use {
                if (it.isSuccessful) {
                    val json = it.toStringMap()
                    val qrCode = json["codeUrl"] as String
                    val orderID = json["orderId"] as String
                    val order = Order(player.uniqueId, orderID, orderName, amount, payType, attach, Date())
                    info("&7用户 &6${player.name} &7发起 &a${payType.translation} &7支付,金额: &6$amount &7订单号: &6$orderID")
                    PlayerInfoCacheManager.getPlayerInfo(player.uniqueId).currentOrder = order
                    OrderCache.orderCache[player.uniqueId] = order
                    OrderCache.groupCache[player.uniqueId] = group
                    player.sendColorMessage(
                        Lang.pay__start.formatByOrder(order)
                    )
                    val qrMap = MapUtil.generateQRMap(qrCode) ?: return@use
                    //默认 5秒检查一次
                    PurchaseChecker(
                        player,
                        order,
                        qrMap,
                        onSuccess
                    ).runTaskTimerAsynchronously(
                        SakuraPurchasePlugin.javaPlugin,
                        Config.queryPeriod,
                        Config.queryPeriod
                    )
                } else {
                    warn("发起支付失败: ${it.code}")
                }
            }
        }.getOrElse {
            warn("发起支付失败 ${it.message}")
            ConnectionManager.isConnected = false
        }
    }

    /**
     * 查询订单状态
     */
    fun query(orderId: String): String {
        val status = "UNKNOWN"
        if (!ConnectionManager.isConnected) return status
        val request = Request.Builder().url("${Config.queryUrl}/$orderId").get().build()
        kotlin.runCatching {
            httpClient.newCall(request).execute().use {
                if (it.isSuccessful) {
                    val s = it.toStringMap()["orderStatusEnum"] as? String
                    if (s != null) return s
                    return status
                }
            }
        }.getOrElse {
            if (SimpleLogger.isDebug) {
                it.printStackTrace()
            }
            ConnectionManager.isConnected = false
        }
        return status
    }

    /**
     * 支付成功时发送保存请求
     */
    fun saveOrder(order: Order): Boolean {
        if (!ConnectionManager.isConnected) return false
        val body = FormBody.Builder()
            .add("uuid", order.uuid.toString())
            .add("orderId", order.orderId)
            .add("_csrf", ConnectionManager.token) //防止跨域攻击
            .build()
        val request = Request.Builder().url(Config.saveUrl).post(body).build()
        kotlin.runCatching {
            httpClient.newCall(request).execute().use {
                return it.isSuccessful
            }
        }.getOrElse {
            it.printStackTrace()
            ConnectionManager.isConnected = false
        }
        return false
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