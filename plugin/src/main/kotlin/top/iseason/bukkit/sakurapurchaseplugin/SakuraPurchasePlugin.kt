package top.iseason.bukkit.sakurapurchaseplugin

import top.iseason.bukkit.sakurapurchaseplugin.command.mainCommand
import top.iseason.bukkit.sakurapurchaseplugin.config.Config
import top.iseason.bukkit.sakurapurchaseplugin.config.Lang
import top.iseason.bukkit.sakurapurchaseplugin.listener.PlayerListener
import top.iseason.bukkit.sakurapurchaseplugin.service.PurchaseService
import top.iseason.bukkittemplate.KotlinPlugin
import top.iseason.bukkittemplate.command.CommandHandler
import top.iseason.bukkittemplate.debug.info
import top.iseason.bukkittemplate.utils.bukkit.EventUtils.register

@Suppress("UNUSED")
object SakuraPurchasePlugin : KotlinPlugin() {
    override fun onAsyncLoad() {

    }

    override fun onEnable() {
//        SimpleLogger.prefix = "&a[&6${javaPlugin.description.name}&a]&r ".toColor()
    }

    override fun onAsyncEnable() {
        Lang.load(false)
        Config.load(false)
        mainCommand()
        CommandHandler.updateCommands()
        PlayerListener.register()

        info("&a插件已启用!")
    }

    override fun onDisable() {
        PurchaseService.purchaseMap.values.forEach { it.cancel() }
        info("&6插件已卸载!")
    }

}