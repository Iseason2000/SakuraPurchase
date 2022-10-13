package top.iseason.bukkit.sakurapurchaseplugin.service

import com.google.gson.Gson
import okhttp3.*
import org.bukkit.entity.Player
import top.iseason.bukkit.sakurapurchaseplugin.SakuraPurchasePlugin
import top.iseason.bukkit.sakurapurchaseplugin.config.Config
import top.iseason.bukkit.sakurapurchaseplugin.config.Config.formatByOrder
import top.iseason.bukkit.sakurapurchaseplugin.config.Lang
import top.iseason.bukkit.sakurapurchaseplugin.data.Order
import top.iseason.bukkit.sakurapurchaseplugin.service.PurchaseService.MyCookieJar.lastToken
import top.iseason.bukkit.sakurapurchaseplugin.util.MapUtil
import top.iseason.bukkittemplate.debug.info
import top.iseason.bukkittemplate.debug.warn
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.sendColorMessage
import java.util.function.Consumer

object PurchaseService {
    val purchaseMap = mutableMapOf<Player, PurchaseChecker>()

    private object MyCookieJar : CookieJar {
        var lastToken: String = ""
            private set
        private val cookieStore: HashMap<String, List<Cookie>> = HashMap()
        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            return cookieStore[url.host] ?: ArrayList()
        }

        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {

            val computeIfAbsent = cookieStore.computeIfAbsent(url.host) { listOf() }.toMutableList()
            //保留新cookie
            for (cookie in cookies) {
                computeIfAbsent.removeIf { it.name == cookie.name }
                computeIfAbsent.add(cookie)
            }
            val tokenCookie = computeIfAbsent.find {
                it.name == "XSRF-TOKEN"
            }
            if (tokenCookie != null) {
                lastToken = tokenCookie.value
            }
            cookieStore[url.host] = computeIfAbsent
        }

        fun clear() = cookieStore.clear()
    }

    private val httpClient = OkHttpClient.Builder().cookieJar(MyCookieJar).build()

    var isConnected: Boolean = false
        private set

    /**
     * 链接登录服务器
     */
    fun connectToServer() {
        MyCookieJar.clear()
        val request = Request.Builder()
            .url(Config.loginUrl)
            .get().build()
        info("&6正在连接至: &7&n${Config.loginUrl}")
        kotlin.runCatching {
            httpClient.newCall(request)
                .execute()
                .use { response ->
                    if (!response.isSuccessful) {
                        warn("服务器链接失败: ${Config.loginUrl} code: ${response.code}")
                        return@use
                    }
                    if (lastToken == "") {
                        warn("获取token失败!")
                        return@use
                    }
                    info("&a获取token: &6$lastToken")
                }
        }.getOrElse {
            warn("服务器链接失败: ${Config.loginUrl} ${it.message}")
            return
        }
        info("&6尝试登录...")
        kotlin.runCatching {
            val formBody = FormBody.Builder()
                .add("username", Config.username)
                .add("password", Config.password)
                .add("_csrf", lastToken)
                .build()
            val loginRequest = Request.Builder()
                .url(Config.loginUrl)
                .post(formBody)
                .build()
            httpClient
                .newCall(loginRequest)
                .execute().close()
        }.getOrElse {
            warn("登陆失败，请检查用户名或密码")
        }

    }

    /**
     * 测试链接可用性
     */
    fun testConnection() {
        info("&6测试连接...")
        kotlin.runCatching {
            httpClient.newCall(
                Request.Builder().url(Config.testUrl).get()
                    .build()
            ).execute().use {
                if ("Success".equals(it.body?.string(), true)) {
                    info("&a链接有效!")
                    isConnected = true
                } else {
                    warn("链接无效,请检查用户名或密码!")
                    isConnected = false
                }
            }
        }.getOrElse {
            warn("链接无效! 请检查 支付服务端链接、用户名或密码")
        }
    }

    /**
     * 为玩家发起支付支付,并启动查询
     */
    fun purchase(
        player: Player,
        amount: Double,
        payType: PayType,
        orderName: String,
        attach: String = "",
        /**
         * 回调，提供 amount
         */
        onSuccess: Consumer<Order>
    ) {

        val body = FormBody.Builder()
            .add("type", payType.type)
            .add("name", orderName)
            .add("amount", amount.toString())
            .add("attach", attach)
            .add("_csrf", lastToken) //防止跨域攻击
            .build()
        val request = Request.Builder().url(Config.purchaseUrl).post(body).build()
        kotlin.runCatching {
            httpClient.newCall(request).execute().use {
                if (it.isSuccessful) {
                    val json = it.toStringMap()
                    val qrCode = json["codeUrl"] as String
                    val orderID = json["orderId"] as String
                    val order = Order(player.uniqueId, orderID, orderName, amount, payType, attach)
                    info("&7用户 &6${player.name} &7发起 &a${payType.translation} &7支付,金额: &6$amount &7订单号: &6$orderID")
                    player.sendColorMessage(
                        Lang.pay__start.formatByOrder(order)
                    )
                    val qrMap = MapUtil.generateQRMap(qrCode) ?: return@use
                    //默认 5秒检查一次
                    PurchaseChecker(
                        player,
                        order,
                        qrMap,
                        onSuccess
                    ).runTaskTimerAsynchronously(
                        SakuraPurchasePlugin.javaPlugin,
                        Config.queryPeriod,
                        Config.queryPeriod
                    )
                } else {
                    warn("发起支付失败: ${it.code}")
                }
            }
        }.getOrElse {
            warn("发起支付失败 ${it.message}")
            isConnected = false
        }
    }

    /**
     * 查询订单状态
     */
    fun query(orderId: String): String {
        val status = "UNKNOWN"
        if (!isConnected) return status
        val request = Request.Builder().url("${Config.queryUrl}/$orderId").get().build()
        kotlin.runCatching {
            httpClient.newCall(request).execute().use {
                if (it.isSuccessful) {
                    val s = it.toStringMap()["orderStatusEnum"] as? String
                    if (s != null) return s
                    return status
                }
            }
        }
        return status
    }

    enum class PayType(
        val type: String,
        val translation: String
    ) {
        //阿里支付
        ALIPAY("ALIPAY_QRCODE", "支付宝"),

        //微信支付
        WXPAY("WXPAY_NATIVE", "微信");
    }

    fun Response.toStringMap() = Gson().fromJson(body!!.string(), Map::class.java)

}