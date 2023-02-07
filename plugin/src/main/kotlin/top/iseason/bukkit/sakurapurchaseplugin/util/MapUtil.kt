package top.iseason.bukkit.sakurapurchaseplugin.util

import com.github.johnnyjayjay.spigotmaps.MapBuilder
import com.github.johnnyjayjay.spigotmaps.RenderedMap
import com.github.johnnyjayjay.spigotmaps.rendering.ImageRenderer
import com.github.johnnyjayjay.spigotmaps.util.Compatibility
import com.github.johnnyjayjay.spigotmaps.util.ImageTools
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.world.WorldSaveEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.map.MapCanvas
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView
import top.iseason.bukkit.sakurapurchaseplugin.config.Config
import top.iseason.bukkittemplate.DisableHook
import top.iseason.bukkittemplate.debug.debug
import top.iseason.bukkittemplate.utils.bukkit.EventUtils.listen
import top.iseason.bukkittemplate.utils.other.submit
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO


object MapUtil {
    private val maps = mutableMapOf<World, MutableList<RenderedMap>>()
    private val emptyRender = object : MapRenderer() {
        override fun render(map: MapView, canvas: MapCanvas, player: Player) {
        }
    }

    //    private val fakeStore = object : MapStorage {
//        private val renders = hashMapOf<Int, MutableList<MapRenderer>>()
//        override fun remove(mapId: Int, renderer: MapRenderer?) {
//            if (renderer == null) return
//            renders[mapId]?.remove(renderer)
//        }
//
//        override fun store(mapId: Int, renderer: MapRenderer?) {
//            if (renderer == null) return
//            println(mapId)
//
//            renders.computeIfAbsent(mapId) { LinkedList() }.add(renderer)
//        }
//
//        override fun provide(mapId: Int): MutableList<MapRenderer> {
//            return renders[mapId] ?: mutableListOf()
//        }
//    }
//
//    init {
//        InitializationListener.register(fakeStore, BukkitTemplate.getPlugin())
//    }
    init {
        listen<WorldSaveEvent> {
            submit(100, 0, true) {
                removeMap(world)
            }
        }
        DisableHook.addTask {
            maps.keys.forEach { removeMap(it) }
        }
    }

    private fun removeMap(world: World) {
        val renderedMaps = maps[world] ?: return
        val iterator = renderedMaps.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            try {
                val id = Compatibility.getId(next.view)
                val file = File(world.worldFolder, "data${File.separatorChar}map_${id}.dat")
                if (file.exists() && file.delete()) {
                    debug("deleted qrcode map file $file")
                    iterator.remove()
                }
            } catch (e: Throwable) {
                continue
            }
        }
        if (renderedMaps.isEmpty()) {
            maps.remove(world)
        }
    }

    private val wechatIcon: BufferedImage? by lazy {
        kotlin.runCatching {
            ImageIO.read(this::class.java.classLoader.getResourceAsStream("icon/wechat.jpg"))
        }.getOrNull()
    }
    private val alipayIcon: BufferedImage? by lazy {
        kotlin.runCatching { ImageIO.read(this::class.java.classLoader.getResourceAsStream("icon/alipay.jpg")) }
            .getOrNull()
    }

    /**
     * 由内容生成地图
     */
    fun generateQRMap(str: String): ItemStack? {
        val icon = if (str.startsWith("weixin")) wechatIcon else alipayIcon
        val image = runCatching {
            QRCodeUtil.generateQRcode(
                str, 512, 512, 128, 128,
                icon, Config.qrColor
            )
        }.getOrElse {
            it.printStackTrace()
            return null
        }
        val renderer = ImageRenderer.builder()
            .renderOnce(true)
            .image(ImageTools.resizeToMapSize(image)) // set the image to render
            .build() // build the instance
        val renderedMap = MapBuilder.create()
//                .store(fakeStore)
            .addRenderers(renderer)
            .build()
        val world = Bukkit.getWorlds().first()
        maps.computeIfAbsent(world) { LinkedList() }.add(renderedMap)
        return renderedMap.createItemStack()
    }

}