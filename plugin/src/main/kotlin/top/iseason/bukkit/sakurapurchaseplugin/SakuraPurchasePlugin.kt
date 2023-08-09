package top.iseason.bukkit.sakurapurchaseplugin

import fr.xephi.authme.events.LoginEvent
import org.bstats.bukkit.Metrics
import org.bukkit.event.player.PlayerLoginEvent
import top.iseason.bukkit.sakurapurchaseplugin.command.mainCommand
import top.iseason.bukkit.sakurapurchaseplugin.config.Config
import top.iseason.bukkit.sakurapurchaseplugin.config.Language
import top.iseason.bukkit.sakurapurchaseplugin.config.OrderCache
import top.iseason.bukkit.sakurapurchaseplugin.hook.AuthMeHook
import top.iseason.bukkit.sakurapurchaseplugin.hook.PAPIExpansion
import top.iseason.bukkit.sakurapurchaseplugin.hook.PAPIHook
import top.iseason.bukkit.sakurapurchaseplugin.listener.PlayerListener
import top.iseason.bukkit.sakurapurchaseplugin.listener.SwapListener
import top.iseason.bukkit.sakurapurchaseplugin.manager.Connection
import top.iseason.bukkit.sakurapurchaseplugin.manager.PurchaseManager
import top.iseason.bukkittemplate.BukkitPlugin
import top.iseason.bukkittemplate.command.CommandHandler
import top.iseason.bukkittemplate.debug.info
import top.iseason.bukkittemplate.utils.bukkit.EventUtils.listen
import top.iseason.bukkittemplate.utils.bukkit.EventUtils.registerListener
import top.iseason.bukkittemplate.utils.bukkit.SchedulerUtils.submit
import java.io.File


object SakuraPurchasePlugin : BukkitPlugin {

    override fun onEnable() {
        Metrics(javaPlugin, 17635)
        PAPIHook.checkHooked()
        AuthMeHook.checkHooked()
        if (PAPIHook.hasHooked) {
            PAPIExpansion.register()
        }
        OrderCache.load()
        Language.load(false)
        Config.load(false)

        mainCommand()
        CommandHandler.updateCommands()
        PlayerListener.registerListener()
        SwapListener.registerListener()
        if (AuthMeHook.hasHooked) {
            listen<LoginEvent> {
                PlayerListener.doOnLogin(this.player)
            }
        } else {
            listen<PlayerLoginEvent> {
                PlayerListener.doOnLogin(this.player)
            }
        }
        info("&a插件已启用!")
        val file = File(javaPlugin.dataFolder, "placeholders.txt")
        if (!file.exists()) {
            javaPlugin.saveResource("placeholders.txt", true)
        }
        submit(async = true) {
            Connection.connectToServer()
            Connection.testConnection()
        }
    }

    override fun onDisable() {
        OrderCache.save()
        PurchaseManager.purchaseMap.values.forEach { it.cancel() }
        info("&6插件已卸载!")
    }

}