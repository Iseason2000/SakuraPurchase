package top.iseason.bukkit.sakurapurchaseplugin.manager

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

internal object LocalCookie : CookieJar {
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