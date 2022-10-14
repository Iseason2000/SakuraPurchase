package top.iseason.bukkit.sakurapurchaseplugin.config

import okhttp3.*
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import top.iseason.bukkit.sakurapurchaseplugin.entity.Order
import top.iseason.bukkit.sakurapurchaseplugin.manager.ConnectionManager
import top.iseason.bukkittemplate.config.SimpleYAMLConfig
import top.iseason.bukkittemplate.config.annotations.Comment
import top.iseason.bukkittemplate.config.annotations.FilePath
import top.iseason.bukkittemplate.config.annotations.Key
import top.iseason.bukkittemplate.debug.SimpleLogger
import top.iseason.bukkittemplate.debug.warn
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.formatBy
import top.iseason.bukkittemplate.utils.other.submit
import java.util.regex.Pattern


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
    @Comment("订单支付状态查询频率,单位tick")
    var queryPeriod: Long = 100

    @Key
    @Comment("发起订单的最小间隔(秒)，设置合适的值以避免刷单")
    var coolDown: Double = 30.0

    @Key
    @Comment(
        "sakurapurchase paycommand 支付方式完成之后运行的命令,以控制台的身份",
        "原生变量为%player%:玩家名, %amount%:充值的金额%, %10_amount%:表示充值的金额X10"
    )
    var purchaseCommand: List<String> = listOf("")
    val pattern = Pattern.compile("(%.*?_?amount%)")

    val loginUrl
        get() = "$serverHost/login"
    val apiUrl
        get() = "$serverHost/api"
    val purchaseUrl
        get() = "$apiUrl/pay/buy"
    val queryUrl
        get() = "$apiUrl/pay/query"
    val testUrl
        get() = "$apiUrl/pay/test"

    val userTotalUrl
        get() = "$apiUrl/record/user-total"
    val userAllUrl
        get() = "$apiUrl/record/user-all"
    val saveUrl get() = "$apiUrl/record/save"

    val totalAmountUrl get() = "$apiUrl/record/all-total"

    override fun onLoaded(section: ConfigurationSection) {
        serverHost = serverHost.removeSuffix("/")
        ConnectionManager.connectToServer()
        ConnectionManager.testConnection()
    }

    /**
     * 格式化接受 Order类型的消息
     */
    fun String.formatByOrder(order: Order) = this.formatBy(
        order.orderId,
        order.orderName,
        order.payType.translation,
        order.amount,
        order.attach,
        order.getStringTime()
    )

    fun performCommands(player: Player, amount: Double) {
        submit {
            for (s in purchaseCommand) {
                var command = s.replace("%player%", player.name)
                val matcher = pattern.matcher(command)
                while (matcher.find()) {
                    val group = matcher.group(1)
                    val split = group.substring(1, group.length - 1).split('_')
                    var multiply = 1.0
                    if (split.size == 2) {
                        multiply = runCatching { split[0].toDouble() }.getOrElse { 1.0 }
                    }
                    command = command.replace(group, (amount * multiply).toString())
                }
                runCatching { Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command) }.getOrElse {
                    warn("命令 $command 执行失败,请打开debug模式查看报错")
                    if (SimpleLogger.isDebug) {
                        it.printStackTrace()
                    }
                }
            }
        }
    }
}