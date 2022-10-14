package top.iseason.bukkit.sakurapurchaseplugin.command

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.permissions.PermissionDefault
import top.iseason.bukkit.sakurapurchaseplugin.config.Config
import top.iseason.bukkit.sakurapurchaseplugin.config.Lang
import top.iseason.bukkit.sakurapurchaseplugin.manager.ConnectionManager
import top.iseason.bukkit.sakurapurchaseplugin.manager.PlayerInfoCacheManager
import top.iseason.bukkit.sakurapurchaseplugin.manager.PurchaseManager
import top.iseason.bukkittemplate.command.*
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.formatBy
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.sendColorMessage
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.sendColorMessages
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
            param("<platform>", suggest = PurchaseManager.PayType.values().map { it.name })
            param("<name>")
            param("[attach]")
            async = true
            val weakCoolDown = WeakCoolDown<Player>()
            executor {
                val player = next<Player>()
                if (!ConnectionManager.isConnected) {
                    player.sendColorMessage(Lang.pay__connection_error)
                    return@executor
                }
                val coolDown = (Config.coolDown * 1000).toLong()
                if (weakCoolDown.check(player, coolDown)) {
                    throw ParmaException(
                        Lang.pay__coolDown.formatBy(
                            weakCoolDown.getCoolDown(
                                player,
                                coolDown
                            ) / 1000.0
                        )
                    )
                }
                val amount = next<Double>()
                if (amount < 0.01) throw ParmaException("支持的最小金额为 0.01 元")
                val type = next<PurchaseManager.PayType>()
                val name = next<String>()
                val attach = nextOrNull<String>() ?: ""
                if (!ConnectionManager.isConnected) throw ParmaException("&e支付服务未启用!")
                PurchaseManager.purchase(player, amount, type, name, attach) {
                    //成功执行命令
                    Config.performCommands(player, amount)
                }
            }
        }
        node("log") {
            async = true
            description = "查询支付过的订单"
            param("[index]", suggest = listOf("1", "2", "3", "4", "5"))
            param("[player]", suggestRuntime = {
                if (isOp) Bukkit.getOnlinePlayers().map { it.name }
                else emptyList()
            })
            executor {
                val page = nextOrNull<Int>() ?: 1
                val player = nextOrNull<Player>()
                if (player != null && !it.isOp) return@executor
                val rp = player ?: it as? Player ?: return@executor
                val playerInfo = PlayerInfoCacheManager.getPlayerInfo(rp.uniqueId)
                val lastOrders = playerInfo.getLastOrders((page - 1) * 5, 5)
                if (lastOrders.isEmpty())
                    it.sendColorMessage(Lang.command__no_record)
                else
                    it.sendColorMessages(lastOrders, prefix = "")
            }
        }
    }

}