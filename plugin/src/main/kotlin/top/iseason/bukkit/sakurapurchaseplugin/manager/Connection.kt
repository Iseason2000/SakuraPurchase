package top.iseason.bukkit.sakurapurchaseplugin.manager

import okhttp3.FormBody
import okhttp3.Headers.Companion.toHeaders
import okhttp3.OkHttpClient
import okhttp3.Request
import org.bukkit.Bukkit
import top.iseason.bukkit.sakurapurchaseplugin.config.Config
import top.iseason.bukkit.sakurapurchaseplugin.util.Result
import top.iseason.bukkittemplate.debug.info
import top.iseason.bukkittemplate.debug.warn
import top.iseason.bukkittemplate.utils.other.EasyCoolDown
import java.util.concurrent.TimeUnit


object Connection {

    val token get() = LocalCookie.lastToken

    private val httpClient = OkHttpClient
        .Builder()
        .cookieJar(LocalCookie)
        .connectTimeout(3, TimeUnit.SECONDS)
        .build()

    var isConnected: Boolean = false
        @Synchronized get() {
            if (!field) {
                if (!EasyCoolDown.check("try_reconnect", 60000)) {
                    info("尝试重新连接服务器")
                    connectToServer()
                    testConnection()
                }
            }
            return field
        }

    /**
     * 链接登录服务器
     */
    fun connectToServer() {
        LocalCookie.clear()
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
                    if (LocalCookie.lastToken == "") {
                        warn("获取token失败!")
                        return@use
                    }
                    info("&a获取token: &6${LocalCookie.lastToken}")
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
                .add("remember-me", "true")
                .add("_csrf", LocalCookie.lastToken)
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
        val version = Bukkit.getServer().version
        val httpGet = httpGet("${Config.testUrl}/$version")
        isConnected = if (httpGet.isSuccess()) {
            info("&a链接有效!")
            true
        } else {
            warn("链接无效! 请检查 支付服务端链接、用户名或密码")
            false
        }
    }

    fun httpPost(url: String, body: Map<String, String>): Result {
        val builder = FormBody.Builder()
        body.forEach { (k, v) ->
            builder.add(k, v)
        }
        val request = Request.Builder().url(url).post(builder.build()).build()
        return kotlin.runCatching {
            httpClient.newCall(request).execute().use {
                if (!it.isSuccessful) Result(999, "请求失败")
                else {
                    Result.fromBody(it.body)
                }
            }.also { isConnected = true }
        }.getOrElse {
            isConnected = false
            Result(999, "请求失败")
        }
    }

    fun httpGet(url: String, body: Map<String, String>? = null): Result {
        var builder = Request.Builder()
            .url(url)
        if (body != null) {
            builder = builder.headers(body.toHeaders())
        }
        return kotlin.runCatching {
            httpClient.newCall(builder.get().build()).execute().use {
                if (!it.isSuccessful) Result(999, "请求失败")
                else {
                    Result.fromBody(it.body)
                }
            }.also { isConnected = true }
        }.getOrElse {
            isConnected = false
            Result(999, "请求失败")
        }
    }
}