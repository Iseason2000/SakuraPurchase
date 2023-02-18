package top.iseason.bukkit.sakurapurchaseplugin.command

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.permissions.PermissionDefault
import top.iseason.bukkit.sakurapurchaseplugin.config.Config
import top.iseason.bukkit.sakurapurchaseplugin.config.Lang
import top.iseason.bukkit.sakurapurchaseplugin.config.OrderCache
import top.iseason.bukkit.sakurapurchaseplugin.manager.Connection
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
            async = true
            default = PermissionDefault.OP
            description = "为玩家运行命令组"
            param("<player>", suggestRuntime = ParamSuggestCache.playerParam)
            param("<group>", suggestRuntime = { Config.commandGroup.keys })
            param("[amount]")
            executor { params, _ ->
                if (!Connection.isConnected) throw ParmaException("服务端未连接!")
                val player = params.next<Player>()
                val group = params.next<String>()
                val commands = Config.commandGroup[group] ?: throw ParmaException("命令组不存在")
                val amount = params.nextOrNull<Double>() ?: 0.01
                Config.performCommands(player, amount, commands)
            }
        }
        node("pay") {
            default = PermissionDefault.OP
            description = "发起支付"
            param("<platform>", suggest = PurchaseManager.PayType.values().map { it.name })
            param("<group>", suggestRuntime = { Config.commandGroup.keys })
            param("<player>", suggestRuntime = ParamSuggestCache.playerParam)
            param("<amount>")
//            param("<name>")
            param("[attach]")
            async = true
            val weakCoolDown = WeakCoolDown<Player>()
            executor { params, sender ->
                if (!Connection.isConnected) throw ParmaException("&e支付服务未启用!")
                val type = params.next<PurchaseManager.PayType>()
                val group = params.next<String>()
                val player = params.next<Player>()
                if (PurchaseManager.purchaseMap.containsKey(player)) {
//                    player.sendColorMessage()
                    if (player != sender) player.sendColorMessage(Lang.pay__exist)
                    throw ParmaException("玩家有尚未支付的订单!")
                }
                // 检查冷却
                val coolDown = (Config.coolDown * 1000).toLong()
                if (weakCoolDown.check(player, coolDown)) {
                    val coolDownMessage = Lang.pay__coolDown.formatBy(
                        Config.coolDown - weakCoolDown.getCoolDown(player).toInt() / 1000
                    )
                    if (player != sender) player.sendColorMessage(coolDownMessage)
                    throw ParmaException(coolDownMessage)
                }
                val commands = Config.commandGroup[group] ?: throw ParmaException("命令组不存在")
                if (!Connection.isConnected) {
                    player.sendColorMessage(Lang.pay__connection_error)
                    return@executor
                }
                val amount = params.next<Double>()
                if (amount < 0.01) throw ParmaException("支持的最小金额为 0.01 元")
//                val name = params.next<String>()
                val attach = params.nextOrNull<String>() ?: ""

                PurchaseManager.purchase(player, amount, type, group, attach, group) {
                    //成功执行命令
                    Config.performCommands(player, amount, commands)
                }
            }
        }

        node("refund") {
            default = PermissionDefault.OP
            description = "给某个订单退款"
            param("<orderId>")
            async = true
            executor { params, sender ->
                if (!Connection.isConnected) throw ParmaException("服务端未连接!")
                val orderId = params.next<String>()
                if (PurchaseManager.refundOrder(orderId)) {
                    sender.sendColorMessage(Lang.refund_success)
                } else sender.sendColorMessage(Lang.refund_failure)
            }
        }

        node("log") {
            async = true
            description = "查询支付过的订单"
            param("[index]", suggest = listOf("1", "2", "3", "4", "5"))
            param("[player]", suggestRuntime = { it ->
                if (it.isOp) Bukkit.getOnlinePlayers().map { it.name }
                else emptyList()
            })
            executor { params, sender ->
                if (!Connection.isConnected) throw ParmaException("服务端未连接!")
                val page = params.nextOrNull<Int>() ?: 1
                var player = params.nextOrNull<Player>()
                if (player != null && !sender.isOp) player = null
                val rp = player ?: sender as? Player ?: return@executor
                val playerInfo = PlayerInfoCacheManager.getPlayerInfo(rp.uniqueId)
                val lastOrders = playerInfo.getLastOrders((page - 1) * 5, 5)
                if (lastOrders.isEmpty())
                    sender.sendColorMessage(Lang.command__no_record)
                else
                    sender.sendColorMessages(lastOrders, prefix = "")
            }
        }
        node("check") {
            description = "手动检查玩家上一个订单是否已支付但未领取"
            default = PermissionDefault.OP
            async = true
            param("<player>", suggestRuntime = ParamSuggestCache.playerParam)
            executor { params, sender ->
                if (!Connection.isConnected) throw ParmaException("服务端未连接!")
                val player = params.next<Player>()
                val order = OrderCache.orderCache[player.uniqueId] ?: throw ParmaException("玩家没有未支付的订单")
                val group = OrderCache.groupCache[player.uniqueId] ?: throw ParmaException("玩家没有未支付的订单")
                val commands = Config.commandGroup[group] ?: throw ParmaException("命令组已失效，请联系管理员")
                sender.sendColorMessage("&e查询玩家未支付的订单...")
                val status = PurchaseManager.query(order.orderId)
                sender.sendColorMessage("&7玩家 &a${player.name} \\n&b$order \\n&e状态: &f$status")
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
            async = true
            default = PermissionDefault.OP
            description = "重新链接支付服务器"
            executor { _, _ ->
                Connection.connectToServer()
                Connection.testConnection()
            }
        }
        node("debug") {
            default = PermissionDefault.OP
            description = "切换调试模式"
            executor { _, _ ->
                SimpleLogger.isDebug = !SimpleLogger.isDebug
            }
        }
    }

}