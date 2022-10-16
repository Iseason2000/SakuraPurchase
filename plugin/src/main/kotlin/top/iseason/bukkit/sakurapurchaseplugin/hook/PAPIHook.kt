package top.iseason.bukkit.sakurapurchaseplugin.hook

import me.clip.placeholderapi.PlaceholderAPI
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import top.iseason.bukkit.sakurapurchaseplugin.manager.PlayerInfoCacheManager
import top.iseason.bukkittemplate.BukkitTemplate
import top.iseason.bukkittemplate.hook.BaseHook

object PAPIHook : BaseHook("PlaceholderAPI") {

    fun setPlaceholder(str: String, player: Player?) =
        if (hasHooked) PlaceholderAPI.setPlaceholders(player, str) else str
}

/**
 * 变量列表
 * 玩家
 *
 * 以下的 [index] 如果等于 0 表示正在支付的订单，否则为最近支付的第 index 个订单
 *
 * sakurapurchase_player_[index]_orderid 支付的订单ID
 *
 * sakurapurchase_player_[index]_ordername 支付的订单名称
 *
 * sakurapurchase_player_[index]_amount 支付的订单金额
 *
 * sakurapurchase_player_[index]_paytype 支付的订单支付类型
 *
 * sakurapurchase_player_[index]_attach 支付的订单附加信息
 *
 * sakurapurchase_player_[index]_createtime 支付的订单创建时间
 *
 * sakurapurchase_player_count 已经支付的订单数
 *
 * sakurapurchase_player_total 总充值金额
 *
 * 全局
 * sakurapurchase_total 服务器总氪金金额
 *
 */
object PAPIExpansion : PlaceholderExpansion() {

    override fun getIdentifier(): String {
        return "sakurapurchase"
    }

    override fun getAuthor(): String {
        return BukkitTemplate.getPlugin().description.authors.joinToString(",")
    }

    override fun getVersion(): String {
        return BukkitTemplate.getPlugin().description.version
    }

    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        val split = params.split('_')
        val type = split.firstOrNull() ?: return null
        if (type == "total") {
            return PlayerInfoCacheManager.totalAmount.toString()
        } else if (type == "player") {
            if (player == null) return null
            val arg2 = split.getOrNull(1) ?: return null
            val playerInfo = PlayerInfoCacheManager.getPlayerInfo(player.uniqueId)
            if (arg2 == "count") return playerInfo.orders.count().toString()
            if (arg2 == "total") return playerInfo.totalAmount.toString()
            val index = arg2.toIntOrNull() ?: return null
            val arg3 = split.getOrNull(2) ?: return null
            val order = (if (index <= 0) playerInfo.lastOrder
            else playerInfo.currentOrder) ?: return null
            return when (arg3) {
                "orderid" -> order.orderId
                "ordername" -> order.orderName
                "amount" -> order.amount.toString()
                "paytype" -> order.payType.translation
                "attach" -> order.attach
                "createtime" -> order.getStringTime()
                else -> null
            }
        }
        return null
    }

}