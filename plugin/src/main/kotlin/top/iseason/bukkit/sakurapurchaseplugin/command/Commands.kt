package top.iseason.bukkit.sakurapurchaseplugin.command

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.permissions.PermissionDefault
import top.iseason.bukkit.sakurapurchaseplugin.config.Config
import top.iseason.bukkit.sakurapurchaseplugin.config.Lang
import top.iseason.bukkit.sakurapurchaseplugin.config.OrderCache
import top.iseason.bukkit.sakurapurchaseplugin.manager.ConnectionManager
import top.iseason.bukkit.sakurapurchaseplugin.manager.PlayerInfoCacheManager
import top.iseason.bukkit.sakurapurchaseplugin.manager.PurchaseManager
import top.iseason.bukkittemplate.command.*
import top.iseason.bukkittemplate.debug.SimpleLogger
import top.iseason.bukkittemplate.debug.warn
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.formatBy
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.sendColorMessage
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.sendColorMessages
import top.iseason.bukkittemplate.utils.other.WeakCoolDown

fun mainCommand() {
    command("sakurapurchase") {
        alias = arrayOf("sp", "sap", "spurchase", "purchase")
        description = "支付插件主命令"

        node("run") {
            default = PermissionDefault.OP
            description = "为玩家运行命令组"
            param("<player>", suggestRuntime = ParamSuggestCache.playerParam)
            param("<group>", suggestRuntime = { Config.commandGroup.keys })
            param("[amount]")
            executor {
                val player = next<Player>()
                val group = next<String>()
                val commands = Config.commandGroup[group] ?: throw ParmaException("命令组不存在")
                val amount = nextOrNull<Double>() ?: 0.01
                Config.performCommands(player, amount, commands)
            }
        }
        node("pay") {
            default = PermissionDefault.OP
            description = "发起支付"
            param("<player>", suggestRuntime = ParamSuggestCache.playerParam)
            param("<group>", suggestRuntime = { Config.commandGroup.keys })
            param("<amount>")
            param("<platform>", suggest = PurchaseManager.PayType.values().map { it.name })
            param("<name>")
            param("[attach]")
            async = true
            val weakCoolDown = WeakCoolDown<Player>()
            executor {
                val player = next<Player>()
                val group = next<String>()
                val commands = Config.commandGroup[group] ?: throw ParmaException("命令组不存在")
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
                PurchaseManager.purchase(player, amount, type, name, attach, group) {
                    //成功执行命令
                    Config.performCommands(player, amount, commands)
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
        node("check") {
            description = "手动检查玩家上一个订单是否已支付但未领取"
            default = PermissionDefault.OP
            async = true
            param("<player>", suggestRuntime = ParamSuggestCache.playerParam)
            executor {
                val player = next<Player>()
                val order = OrderCache.orderCache[player.uniqueId] ?: throw ParmaException("玩家没有未支付的订单")
                val group = OrderCache.groupCache[player.uniqueId] ?: throw ParmaException("玩家没有未支付的订单")
                val commands = Config.commandGroup[group] ?: throw ParmaException("命令组已失效，请联系管理员")
                it.sendColorMessage("&e查询中...")
                val status = PurchaseManager.query(order.orderId)
                it.sendColorMessage("&7玩家 &a${player.name} 具有订单: &b$order 状态: &f$status")
                if (status == "SUCCESS") {
                    if (!PurchaseManager.saveOrder(order)) {
                        warn("保存订单异常，请检查链接!")
                    }
                    //加入缓存
                    val playerInfo = PlayerInfoCacheManager.getPlayerInfo(player.uniqueId)
                    Config.performCommands(player, order.amount, commands)
                    playerInfo.orders.add(order)
                    playerInfo.currentOrder = null
                    OrderCache.orderCache.remove(player.uniqueId)
                    OrderCache.groupCache.remove(player.uniqueId)
                    playerInfo.lastOrder = order
                    PlayerInfoCacheManager.modifyCache += order.amount
                }
            }
        }
        node("reConnect") {
            default = PermissionDefault.OP
            description = "重新链接支付服务器"
            executor {
                ConnectionManager.connectToServer()
                ConnectionManager.testConnection()
            }
        }
        node("debug") {
            default = PermissionDefault.OP
            description = "切换调试模式"
            executor {
                SimpleLogger.isDebug = !SimpleLogger.isDebug
            }
        }
    }

}