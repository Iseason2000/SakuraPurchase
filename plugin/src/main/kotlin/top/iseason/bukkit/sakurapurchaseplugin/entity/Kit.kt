package top.iseason.bukkit.sakurapurchaseplugin.entity

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import top.iseason.bukkittemplate.utils.bukkit.ItemUtils
import top.iseason.bukkittemplate.utils.bukkit.ItemUtils.toBase64
import top.iseason.bukkittemplate.utils.bukkit.ItemUtils.toSection

/**
 * 支付完的反馈
 */
class Kit(
    /**
     * 礼包的名称
     */
    val name: String,
) {
    /**
     * 礼包的描述
     */
    var info: String? = null
        private set

    /**
     * 给予物品
     */
    var items = listOf<ItemStack>()
        private set

    /**
     * 给予命令
     */
    var commands = listOf<String>()
        private set

    /**
     * 储存到配置文件
     */
    fun toSection(encrypt: Boolean = true): ConfigurationSection {
        val yaml = YamlConfiguration()
        yaml["name"] = name
        yaml["info"] = info
        if (items.isNotEmpty())
            yaml["items"] = if (encrypt) items.toBase64() else items.toSection(true)
        if (commands.isNotEmpty())
            yaml["commands"] = commands
        return yaml
    }

    companion object {
        fun fromSection(section: ConfigurationSection): Kit? {
            val name = section.getString("name") ?: return null
            val kit = Kit(name)
            section.getString("info")?.also { kit.info = it }
            val itemBase64 = section.getString("items")
            if (itemBase64 != null) {
                kit.items = ItemUtils.fromBase64ToItems(itemBase64)
            } else {
                val items = section.getList("items")?.let { ItemUtils.fromSections(it) }
                if (!items.isNullOrEmpty())
                    kit.items = items
            }
            kit.commands = section.getStringList("commands")
            return kit
        }
    }
}