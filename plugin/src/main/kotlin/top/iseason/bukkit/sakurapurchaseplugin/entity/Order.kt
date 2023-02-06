package top.iseason.bukkit.sakurapurchaseplugin.entity

import com.google.gson.JsonObject
import org.bukkit.configuration.ConfigurationSection
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
                val asString = json["platformEnum"].asString
                val platform = if (asString == "WX") PurchaseManager.PayType.WXPAY else PurchaseManager.PayType.ALIPAY
                Order(
                    uuid,
                    json["orderId"].asString,
                    json["orderName"].asString,
                    json["orderAmount"].asDouble,
                    platform,
                    json["attach"].asString,
                    format.parse(json["createTime"].asString)
                )
            } catch (e: Throwable) {
//                e.printStackTrace()
                null
            }
        }

        fun fromSection(uuid: UUID, section: ConfigurationSection): Order? {
            return try {
                Order(
                    uuid,
                    section.getString("orderId")!!,
                    section.getString("orderName")!!,
                    section.getDouble("amount"),
                    PurchaseManager.PayType.values()[section.getInt("payType")],
                    section.getString("attach")!!,
                    format.parse(section.getString("createTime"))
                )
            } catch (e: Throwable) {
                null
            }
        }
    }

    fun getStringTime() = format.format(createTime)

    override fun toString(): String {
        return Lang.command__order_format.formatByOrder(this).toString()
    }

    fun toSection(section: ConfigurationSection) {
        section["orderId"] = orderId
        section["orderName"] = orderName
        section["amount"] = amount
        section["payType"] = payType.ordinal
        section["attach"] = attach
        section["createTime"] = getStringTime()
    }

}