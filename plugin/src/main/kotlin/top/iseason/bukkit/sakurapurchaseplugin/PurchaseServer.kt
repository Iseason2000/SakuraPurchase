package top.iseason.bukkit.sakurapurchaseplugin

import okhttp3.*
import org.bukkit.entity.Player
import top.iseason.bukkittemplate.debug.info
import top.iseason.bukkittemplate.debug.warn

object PurchaseServer {

    private object MyCookieJar : CookieJar {
        private val cookieStore: HashMap<String, List<Cookie>> = HashMap()
        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            return cookieStore[url.host] ?: ArrayList()
        }

        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            val computeIfAbsent = cookieStore.computeIfAbsent(url.host) { listOf() }.toMutableList()
            //保留新cookie
            computeIfAbsent.addAll(cookies)
            computeIfAbsent.reverse()
            computeIfAbsent.distinctBy { it.name }
            cookieStore[url.host] = computeIfAbsent
        }

        fun clear() = cookieStore.clear()
    }

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
        info("&6尝试登录: &7&n${Config.loginUrl}")
        var token: String? = null
        getHttpClient().newCall(request)
            .execute()
            .use { response ->
                if (!response.isSuccessful) {
                    warn("服务器链接失败: ${Config.loginUrl} code: ${response.code}")
                    return@use
                }
                token = response.headers("Set-Cookie").find { it.startsWith("XSRF-TOKEN=") }?.substring(11, 47)
                if (token == null) {
                    warn("获取token失败!")
                    return@use
                }
                info("&a获取token: &6$token")
            }
        if (token == null) return
        info("&6尝试登录...")
        kotlin.runCatching {
            val formBody = FormBody.Builder()
                .add("username", Config.username)
                .add("password", Config.password)
                .add("_csrf", token!!)
                .build()
            val loginRequest = Request.Builder()
                .url(Config.loginUrl)
                .post(formBody)
                .build()
            getHttpClient()
                .newCall(loginRequest)
                .execute().close()
        }.getOrElse {
            it.printStackTrace()
            warn("登陆失败，请检查用户名或密码")
        }

    }

    /**
     * 测试链接可用性
     */
    fun testConnection() {
        info("&6测试连接...")
        kotlin.runCatching {
            getHttpClient().newCall(
                Request.Builder().url("http://localhost/api/test").get()
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
            it.printStackTrace()
            warn("登录失败,请检查用户名或密码!")
        }
    }

    /**
     * 获取插件带cookie缓存的Hppt客户端
     */
    fun getHttpClient() = OkHttpClient.Builder().cookieJar(MyCookieJar).build()

    /**
     * 支付完运行命令
     */
    fun purchaseCommand(player: Player, amount: Double, payType: PayType, orderName: String, attach: String = "") {
        val body = FormBody.Builder()
            .add("payType", payType.type)
            .add("orderName", orderName)
            .add("amount", amount.toString())
            .add("attach", attach)
            .build()
        val request = Request.Builder().url(Config.purchaseUrl).post(body).build()
        getHttpClient().newCall(request).execute().use {
            if (it.isSuccessful) {
                println(it.body!!.string())
                info("&7用户 &6${player.name} &7发起 &a${payType.translation} &7支付,金额: &6$amount 订单")
            } else {
                warn("发起支付失败")
            }
        }

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
}