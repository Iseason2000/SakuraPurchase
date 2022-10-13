package top.iseason.bukkit.sakurapurchaseplugin

import org.bukkit.entity.Player
import top.iseason.bukkit.sakurapurchaseplugin.service.PurchaseService
import top.iseason.bukkittemplate.command.*

fun mainCommand() {

    command("sakurapurchase") {
        alias = arrayOf("sp", "sap", "spurchase")
        description = "支付插件主命令"
        node("paycommand") {
            alias = arrayOf("pc", "payc")
            description = "支付完执行命令"
            param("<player>", suggestRuntime = ParamSuggestCache.playerParam)
            param("<amount>")
            param("<platform>", suggest = PurchaseService.PayType.values().map { it.name })
            param("<name>")
            param("[attach]")
            async = true
            executor {
                val player = next<Player>()
                val amount = next<Double>()
                val type = next<PurchaseService.PayType>()
                val name = next<String>()
                val attach = nextOrNull<String>() ?: ""
                if (!PurchaseService.isConnected) throw ParmaException("&e支付服务未启用!")
                PurchaseService.purchase(player, amount, type, name, attach)

            }
        }
    }

}