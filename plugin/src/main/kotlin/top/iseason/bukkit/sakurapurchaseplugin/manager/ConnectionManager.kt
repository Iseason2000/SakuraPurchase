package top.iseason.bukkit.sakurapurchaseplugin.manager

import okhttp3.*
import org.bukkit.Bukkit
import top.iseason.bukkit.sakurapurchaseplugin.config.Config
import top.iseason.bukkittemplate.debug.info
import top.iseason.bukkittemplate.debug.warn
import java.util.concurrent.TimeUnit

object ConnectionManager {
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

    val token get() = MyCookieJar.lastToken

    val httpClient = OkHttpClient
        .Builder()
        .cookieJar(MyCookieJar)
        .connectTimeout(3, TimeUnit.SECONDS)
        .build()

    var isConnected: Boolean = false

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
                    if (MyCookieJar.lastToken == "") {
                        warn("获取token失败!")
                        return@use
                    }
                    info("&a获取token: &6${MyCookieJar.lastToken}")
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
                .add("_csrf", MyCookieJar.lastToken)
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
            val version = Bukkit.getServer().version
            httpClient.newCall(
                Request.Builder().url("${Config.testUrl}/$version").get()
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
}