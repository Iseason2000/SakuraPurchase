package top.iseason.bukkit.sakurapurchaseplugin.data

import top.iseason.bukkit.sakurapurchaseplugin.service.PurchaseService
import java.util.*

data class Order(
    val uuid: UUID,
    val orderId: String,
    val orderName: String,
    val amount: Double,
    val payType: PurchaseService.PayType,
    val attach: String = "",
) {

}