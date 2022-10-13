@file:Suppress("UNCHECKED_CAST", "DEPRECATION")

package top.iseason.bukkittemplate.command

import com.google.common.base.Enums
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType
import java.util.*
import kotlin.reflect.KClass

/**
 * 参数适配器，负责将参数转为对应类的对象
 */
@Suppress("unused")
open class ParamAdopter<T : Any>(
    val type: KClass<T>,
    var errorMessage: String = "Param %s is not exist",
    val onCast: ParamTransform<T>
) {
    fun interface ParamTransform<T> {
        fun onCast(param: String): T?
    }

    companion object {
        private val paramsAdopter = mutableMapOf<KClass<*>, ParamAdopter<*>>()

        init {
            setDefaultParams()
        }

        /**
         * 添加一个参数适配器
         */
        fun addTypeParam(paramAdopter: ParamAdopter<*>) {
            paramsAdopter[paramAdopter.type] = paramAdopter
        }

        /**
         * 获取负责转换某类型的适配器
         */
        fun getTypeParam(clazz: KClass<*>) = paramsAdopter[clazz]

        /**
         * 删除某类型的参数适配器
         */
        fun removeType(type: KClass<*>) = paramsAdopter.remove(type)

        /**
         * 获取转换后的参数，
         * @return 转换后的参数，不存在则null
         */
        fun <T> getOptionalTypedParam(clazz: KClass<*>, paramStr: String): T? {
            val typeParam = paramsAdopter[clazz]
            //匹配所有枚举
            if (typeParam == null && clazz.java.isEnum) {
                return Enums.getIfPresent(clazz.java as Class<out Enum<*>>, paramStr.uppercase()).orNull() as? T
            }
            if (typeParam == null) throw ParmaException("Param Type is not exist!")
            return typeParam.onCast.onCast(paramStr) as? T
        }

        /**
         * 获取转换后的参数，
         * @return 转换后的参数，不存在则
         * @throws ParmaException
         */
        fun <T> getTypedParam(clazz: KClass<*>, paramStr: String): T {
            return getOptionalTypedParam(clazz, paramStr) ?: throw ParmaException(paramStr, paramsAdopter[clazz])
        }
    }

    /**
     * 注册本适配器
     */
    fun register() = addTypeParam(this)
}

/**
 * 默认提供的参数
 */
private fun setDefaultParams() {
    ParamAdopter(Player::class, errorMessage = "&7玩家 &c%s &7不存在!") {
        if (it.length == 36) {
            runCatching { Bukkit.getPlayer(UUID.fromString(it)) }.getOrNull()
        } else Bukkit.getPlayerExact(it)
    }.register()
    ParamAdopter(OfflinePlayer::class, errorMessage = "&7玩家 &c%s &7不存在!") {
        var player: OfflinePlayer? = Bukkit.getOfflinePlayer(it)
        if (!player!!.hasPlayedBefore()) {
            player = runCatching {
                val offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(it))
                if (offlinePlayer.hasPlayedBefore()) offlinePlayer else null
            }.getOrNull()
        }
        player
    }.register()
    ParamAdopter(Int::class, errorMessage = "&c%s &7不是一个有效的整数") {
        runCatching { it.toInt() }.getOrNull()
    }.register()
    ParamAdopter(Double::class, errorMessage = "&c%s &7不是一个有效的小数") {
        runCatching { it.toDouble() }.getOrNull()
    }.register()
    ParamAdopter(Float::class, errorMessage = "&c%s &7不是一个有效的小数") {
        runCatching { it.toFloat() }.getOrNull()
    }.register()
    ParamAdopter(Long::class, errorMessage = "&c%s &7不是一个有效的长整数") {
        runCatching { it.toLong() }.getOrNull()
    }.register()
    ParamAdopter(String::class) { it }.register()
    ParamAdopter(
        PotionEffectType::class,
        "&c%s &7不是一个有效的药水种类"
    ) {
        PotionEffectType.getByName(it)
    }.register()

}