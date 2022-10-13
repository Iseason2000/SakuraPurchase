package top.iseason.bukkit.sakurapurchaseplugin

import top.iseason.bukkittemplate.KotlinPlugin
import top.iseason.bukkittemplate.debug.info

@Suppress("UNUSED")
object SakuraPurchasePlugin : KotlinPlugin() {
    override fun onAsyncLoad() {

    }

    override fun onEnable() {
//        SimpleLogger.prefix = "&a[&6${javaPlugin.description.name}&a]&r ".toColor()
    }

    override fun onAsyncEnable() {
        Config.load(false)

//        Config.load()
//        openUICommand()
//        UIListener.register()
        //命令
//        openUICommand()
//        command1()
//
//        //如果使用命令模块，取消注释
//        CommandHandler.updateCommands()
        //如果使用UI模块,取消注释
//        registerListeners(UIListener)\
        //使用数据库请取消注释以下2行
//        DatabaseConfig.load(false)
//        DatabaseConfig.initTables()
//        CommandHandler.updateCommands()
//        SimpleYAMLConfig.notifyMessage = "&7配置文件 &6%s &7已重载!"
//        Config.load(false)
//        LagCatcher.performanceCheck("test", 0) {
//            DependencyDownloader().addRepositories("https://maven.aliyun.com/repository/public")
//                .downloadDependency("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
//        }

        info("&a插件已启用!")
    }

    override fun onDisable() {
        info("&6插件已卸载!")
    }

}