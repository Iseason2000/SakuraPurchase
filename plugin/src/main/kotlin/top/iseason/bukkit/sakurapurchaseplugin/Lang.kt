package top.iseason.bukkit.sakurapurchaseplugin

import org.bukkit.configuration.ConfigurationSection
import top.iseason.bukkittemplate.config.SimpleYAMLConfig
import top.iseason.bukkittemplate.config.annotations.FilePath
import top.iseason.bukkittemplate.config.annotations.Key
import top.iseason.bukkittemplate.debug.info

@Key
@FilePath("lang.yml")
object Lang : SimpleYAMLConfig(updateNotify = false) {
    var hello_message = "你好 世界"
    var welcome_message = "欢迎来到我的世界"
    var quit_message = "玩家 %player% 已退出了服务器"

    override fun onLoaded(section: ConfigurationSection) {
        info("语言文件已重载")
    }
}