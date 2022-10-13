package top.iseason.bukkit.sakurapurchaseplugin

import okhttp3.*
import org.bukkit.configuration.ConfigurationSection
import top.iseason.bukkittemplate.config.SimpleYAMLConfig
import top.iseason.bukkittemplate.config.annotations.Comment
import top.iseason.bukkittemplate.config.annotations.FilePath
import top.iseason.bukkittemplate.config.annotations.Key


@FilePath("config.yml")
object Config : SimpleYAMLConfig() {

    @Key
    @Comment("支付服务端地址")
    var serverHost = "http://localhost"

    @Key
    @Comment("支付服务端用户名")
    var username = "test"

    @Key
    @Comment("支付服务端密码")
    var password = "123456"

    @Key
    @Comment("最大支付超时时间,单位秒")
    var maxTimeout: Double = 60.0

    @Key
    @Comment("sakurapurchase paycommand 支付方式完成之后运行的命令")
    var purchaseCommand: List<String> = listOf("")

    val loginUrl
        get() = "${serverHost}/login"

    val purchaseUrl
        get() = "${serverHost}/api/pay"

    override fun onLoaded(section: ConfigurationSection) {
        serverHost = serverHost.removeSuffix("/")
        PurchaseServer.connectToServer()
        PurchaseServer.testConnection()
    }

    override fun onSaved(section: ConfigurationSection) {
        println("saved")
    }
}