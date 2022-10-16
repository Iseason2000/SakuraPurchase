package top.iseason.bukkit.sakurapurchaseplugin.config

import okhttp3.*
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import top.iseason.bukkit.sakurapurchaseplugin.entity.Order
import top.iseason.bukkit.sakurapurchaseplugin.hook.PAPIHook
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
    @Comment("", "支付服务端用户名")
    var username = "test"

    @Key
    @Comment("", "支付服务端密码")
    var password = "123456"

    @Key
    @Comment("", "最大支付超时时间,单位秒")
    var maxTimeout: Double = 60.0

    @Key
    @Comment("", "订单支付状态查询频率,单位tick")
    var queryPeriod: Long = 100

    @Key
    @Comment("", "发起订单的最小间隔(秒)，设置合适的值以避免刷单")
    var coolDown: Double = 30.0

    @Key
    @Comment("", "取消支付的关键词")
    var cancelWorld = listOf("cancel", "取消")

    @Key("command-group")
    @Comment(
        "",
        "sakurapurchase pay 完成之后运行的命令(分组),以控制台的身份",
        "原生变量为%player%:玩家名, %amount%:充值的金额%, %10_amount%:表示充值的金额X10"
    )
    var commandGroup = mutableMapOf("default" to listOf("say helloWorld!"))

    @Key
    @Comment("", "二维码颜色 R,G,B")
    var qrColorStr: String = "0,0,0"
    var qrColor: Int = -0XFFFFFFF

    @Comment("", "支付时的取消动作,默认 SHIFT_F", "SHIFT_F: 蹲下+F 取消", "HEAD_UP: 抬头取消")
    @Key
    var cancelAction = "SHIFT_F"
    private val pattern = Pattern.compile("(%[0-9|.]*?_?amount%)")

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
        if (cancelAction !in listOf("SHIFT_F", "HEAD_UP")) cancelAction = "SHIFT_F"
        serverHost = serverHost.removeSuffix("/")
        val split = qrColorStr.replace(" ", "").split(',')
        qrColor = kotlin.runCatching { Color.fromRGB(split[0].toInt(), split[1].toInt(), split[2].toInt()).asRGB() }
            .getOrElse {
                warn("颜色格式: $qrColorStr 不正确")
                -0XFFFFFFF
            }
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

    fun performCommands(player: Player, amount: Double, commands: List<String>) {
        submit {
            for (s in commands) {
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
                command = PAPIHook.setPlaceholder(command, player)
                runCatching {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)
                }.getOrElse {
                    if (SimpleLogger.isDebug) {
                        it.printStackTrace()
                    } else warn("&命令 $command 执行失败,请打开debug模式查看报错")
                }
            }
        }
    }
}