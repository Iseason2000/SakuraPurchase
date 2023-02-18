package top.iseason.bukkit.sakurapurchaseplugin.util

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import okhttp3.ResponseBody
import top.iseason.bukkit.sakurapurchaseplugin.manager.Connection

data class Result(
    val state: Int,
    val message: String,
    val data: JsonElement? = null
) {
    fun isSuccess() = state == 200

    companion object {
        fun fromBody(body: ResponseBody?): Result {
            if (body == null) return Result(999, "请求失败")
            return kotlin.runCatching {
                val parseString = JsonParser().parse(body.string())
                val asJsonObject = parseString.asJsonObject
                Result(
                    asJsonObject["state"].asInt,
                    asJsonObject["message"].asString,
                    asJsonObject["data"]
                )
            }.getOrElse {
                Connection.isConnected = false
                it.printStackTrace()
                Result(998, "序列化请求失败")
            }
        }
    }
}