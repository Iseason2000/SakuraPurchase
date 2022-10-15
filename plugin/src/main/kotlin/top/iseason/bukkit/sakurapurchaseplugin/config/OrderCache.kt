package top.iseason.bukkit.sakurapurchaseplugin.config

import org.bukkit.configuration.file.YamlConfiguration
import top.iseason.bukkit.sakurapurchaseplugin.entity.Order
import top.iseason.bukkittemplate.BukkitTemplate
import java.io.File
import java.util.*

/**
 * 玩家上一个创建但未完成的订单
 */
object OrderCache {
    private val file = File(BukkitTemplate.getPlugin().dataFolder, "cache.yml")
    var orderCache = mutableMapOf<UUID, Order>()
        private set
    var groupCache = mutableMapOf<UUID, String>()
        private set

    fun save() {
        val yaml = YamlConfiguration()
        for ((k, v) in orderCache) {
            v.toSection(yaml.createSection(k.toString()))
        }
        for ((k, v) in groupCache) {
            yaml["$k.group"] = v
        }
        yaml.save(file)
    }

    fun load() {
        if (!file.exists()) {
            return
        }
        val map = mutableMapOf<UUID, Order>()
        val gmap = mutableMapOf<UUID, String>()
        val yml = YamlConfiguration.loadConfiguration(file)
        yml.getKeys(false).forEach {
            kotlin.runCatching {
                val uuid = UUID.fromString(it)
                val yaml = yml.getConfigurationSection(it)!!
                val order = Order.fromSection(uuid, yaml)!!
                gmap[uuid] = yaml.getString("group")!!
                map[uuid] = order
            }
        }
        orderCache = map
        groupCache = gmap
    }
}
