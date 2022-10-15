package top.iseason.bukkit.sakurapurchaseplugin.config

import org.bukkit.configuration.ConfigurationSection
import top.iseason.bukkittemplate.BukkitTemplate
import top.iseason.bukkittemplate.config.SimpleYAMLConfig
import top.iseason.bukkittemplate.config.annotations.Comment
import top.iseason.bukkittemplate.config.annotations.FilePath
import top.iseason.bukkittemplate.config.annotations.Key
import top.iseason.bukkittemplate.debug.SimpleLogger
import top.iseason.bukkittemplate.debug.info
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils

@Key
@FilePath("lang.yml")
object Lang : SimpleYAMLConfig(updateNotify = false) {
    @Comment("所有消息的前缀")
    var prefix = "&a[&6${BukkitTemplate.getPlugin().description.name}&a] &f"

    @Comment("", "参数以{index}声明,index的范围=[0,5]: orderId, orderName, payType, amount, attach, createTIme")
    var pay = ""
    var pay__start = "&a请使用 &6{2} &a支付.."
    var pay__sucess = "&a支付成功! 金额: &6{3} 元"
    var pay__cancel = "&6支付已取消!"
    var pay__timeout = "&6支付超时!"
    var pay__waiting = "&6请使用 &a{2} &6支付，&bShift+F 取消支付. &c请勿在订单取消后才完成支付."
    var pay__command_block = "&6请先完成支付!"
    var pay__connection_error = "&6支付服务异常，请联系管理员!"

    @Comment("只有一个参数{0}为冷却剩余秒数")
    var pay__coolDown = "&6请等待 &a{0} &6秒后再次发起订单!"

    var command__no_record = "&e没有记录!"

    @Comment(
        "玩家订单格式化",
        "参数以{index}声明,index的范围=[0,5]: orderId, orderName, payType, amount, attach, createTIme"
    )
    var command__order_format =
        "&7订单: &6 {0} &7名称:&f {1} \\n&7支付方式: &e{2} &7金额: &a{3} &7创建时间:&b {5} \\n&7其他:&f{4}\\n "

    override fun onLoaded(section: ConfigurationSection) {
        SimpleLogger.prefix = prefix
        MessageUtils.defaultPrefix = prefix
        info("&a语言文件已重载!")
    }

}