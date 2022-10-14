package top.iseason.bukkit.sakurapurchaseplugin.entity

import com.google.gson.JsonObject
import top.iseason.bukkit.sakurapurchaseplugin.config.Config.formatByOrder
import top.iseason.bukkit.sakurapurchaseplugin.config.Lang
import top.iseason.bukkit.sakurapurchaseplugin.manager.PurchaseManager
import java.text.SimpleDateFormat
import java.util.*

/**
 * 订单
 */
data class Order(
    val uuid: UUID,
    val orderId: String,
    val orderName: String,
    val amount: Double,
    val payType: PurchaseManager.PayType,
    val attach: String,
    val createTime: Date
) {
    companion object {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        fun from(uuid: UUID, json: JsonObject): Order? {
            return try {
                Order(
                    uuid,
                    json["orderId"].asString,
                    json["orderName"].asString,
                    json["orderAmount"].asDouble,
                    PurchaseManager.PayType.values()[json["platform"].asInt],
                    json["attach"].asString,
                    format.parse(json["createTime"].asString)
                )
            } catch (e: Throwable) {
                null
            }
        }
    }

    fun getStringTime() = format.format(createTime)

    override fun toString(): String {
        return Lang.command__order_format.formatByOrder(this)
    }
}