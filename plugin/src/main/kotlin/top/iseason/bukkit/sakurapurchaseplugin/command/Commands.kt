package top.iseason.bukkit.sakurapurchaseplugin.command

import org.bukkit.entity.Player
import org.bukkit.permissions.PermissionDefault
import top.iseason.bukkit.sakurapurchaseplugin.config.Config
import top.iseason.bukkit.sakurapurchaseplugin.config.Lang
import top.iseason.bukkit.sakurapurchaseplugin.service.PurchaseService
import top.iseason.bukkittemplate.command.*
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.formatBy
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.sendColorMessage
import top.iseason.bukkittemplate.utils.other.WeakCoolDown

fun mainCommand() {
    command("sakurapurchase") {
        alias = arrayOf("sp", "sap", "spurchase")
        description = "支付插件主命令"
        node("testcommand") {
            default = PermissionDefault.OP
            isPlayerOnly = true
            description = "测试支付完成执行的命令"
            param("[amount]")
            executor {
                val nextOrNull = nextOrNull<Double>() ?: 0.01
                Config.performCommands(it as Player, nextOrNull)
            }
        }
        node("payForCommand") {
            default = PermissionDefault.OP
            alias = arrayOf("pfc")
            description = "支付完执行命令"
            param("<player>", suggestRuntime = ParamSuggestCache.playerParam)
            param("<amount>")
            param("<platform>", suggest = PurchaseService.PayType.values().map { it.name })
            param("<name>")
            param("[attach]")
            async = true
            val weakCoolDown = WeakCoolDown<Player>()
            executor {
                val player = next<Player>()
                if (!PurchaseService.isConnected) {
                    player.sendColorMessage(Lang.pay__connection_error)
                    return@executor
                }
                val coolDown = (Config.coolDown * 1000).toLong()
                if (weakCoolDown.check(player, coolDown)) {
                    throw ParmaException(
                        Lang.pay_coolDown.formatBy(
                            weakCoolDown.getCoolDown(
                                player,
                                coolDown
                            ) / 1000.0
                        )
                    )
                }
                val amount = next<Double>()
                if (amount < 0.01) throw ParmaException("支持的最小金额为 0.01 元")
                val type = next<PurchaseService.PayType>()
                val name = next<String>()
                val attach = nextOrNull<String>() ?: ""
                if (!PurchaseService.isConnected) throw ParmaException("&e支付服务未启用!")
                PurchaseService.purchase(player, amount, type, name, attach) {
                    //成功执行命令
                    Config.performCommands(player, amount)
                }
            }
        }
    }

}