/*
 * Description:
 * @Author: Iseason2000
 * @Date: 2023/6/10 下午8:16
 *
 */

package top.iseason.bukkittemplate.hook

import io.th0rgal.oraxen.api.OraxenItems
import org.bukkit.inventory.ItemStack
import top.iseason.bukkittemplate.utils.bukkit.ItemUtils

object OraxenHook : BaseHook("Oraxen") {
    init {
        checkHooked()
        if (hasHooked) ItemUtils.itemProviders.add { getByNameId(it) }
    }

    fun isOraxenItem(item: ItemStack) = OraxenItems.getIdByItem(item) != null
    fun getOraxenItemId(item: ItemStack): String? = OraxenItems.getIdByItem(item)
    fun getByNameId(id: String) = OraxenItems.getItemById(id).build()
}
