package top.iseason.bukkit.sakurapurchaseplugin.util

import com.github.johnnyjayjay.spigotmaps.MapBuilder
import com.github.johnnyjayjay.spigotmaps.RenderedMap
import com.github.johnnyjayjay.spigotmaps.rendering.ImageRenderer
import com.github.johnnyjayjay.spigotmaps.util.Compatibility
import com.github.johnnyjayjay.spigotmaps.util.ImageTools
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.world.WorldSaveEvent
import org.bukkit.inventory.ItemStack
import top.iseason.bukkit.sakurapurchaseplugin.config.Config
import top.iseason.bukkit.sakurapurchaseplugin.entity.Order
import top.iseason.bukkit.sakurapurchaseplugin.event.QRCodePreGenerateEvent
import top.iseason.bukkit.sakurapurchaseplugin.event.QRMapGenerateEvent
import top.iseason.bukkit.sakurapurchaseplugin.manager.PurchaseManager.PayType.ALIPAY
import top.iseason.bukkit.sakurapurchaseplugin.manager.PurchaseManager.PayType.WXPAY
import top.iseason.bukkittemplate.BukkitTemplate
import top.iseason.bukkittemplate.DisableHook
import top.iseason.bukkittemplate.debug.debug
import top.iseason.bukkittemplate.utils.bukkit.EventUtils.listen
import top.iseason.bukkittemplate.utils.bukkit.SchedulerUtils.submit
import java.awt.Color
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO


object MapUtil {
    private val maps = mutableMapOf<World, MutableList<RenderedMap>>()

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
            } catch (_: Throwable) {
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
    fun generateQRMap(str: String, player: Player, order: Order): ItemStack? {
        val icon = when (order.payType) {
            ALIPAY -> alipayIcon
            WXPAY -> wechatIcon
        }
        val qrEvent = QRCodePreGenerateEvent(str, 512, 512, 128, 128, icon, Config.qrColor, player, order)
        Bukkit.getPluginManager().callEvent(qrEvent)
        var image = runCatching {
            QRCodeUtil.generateQRcode(
                qrEvent.content, qrEvent.qrWidth, qrEvent.qrHeight, qrEvent.logoWidth, qrEvent.logoHeight,
                qrEvent.logo, qrEvent.qrColor
            )
        }.getOrElse {
            it.printStackTrace()
            return null
        }
        if (qrEvent.isResize) {
            image = ImageTools.resizeToMapSize(image)
        }
        if (Config.qrScale != 1.0) {
            image = scaleImageWithWhitePadding(image, Config.qrScale)
        }
        val qrMapEvent = QRMapGenerateEvent(str, image, player, order)
        Bukkit.getPluginManager().callEvent(qrMapEvent)
        if (qrMapEvent.isCancelled) return ItemStack(Material.AIR)
        val renderer = ImageRenderer.builder()
            .renderOnce(true)
            .image(image) // set the image to render
            .build() // build the instance
        val renderedMap = Bukkit.getScheduler().callSyncMethod(BukkitTemplate.getPlugin()) {
            MapBuilder.create()
//                .store(fakeStore)
                .addRenderers(renderer)
                .build()
        }.get()
        val world = Bukkit.getWorlds().first()
        maps.computeIfAbsent(world) { LinkedList() }.add(renderedMap)
        return renderedMap.createItemStack()
    }

    fun scaleImageWithWhitePadding(srcImage: BufferedImage, scale: Double): BufferedImage {
        val width = srcImage.getWidth(null)
        val height = srcImage.getHeight(null)

        // 创建一个新的 BufferedImage，背景为白色
        val outputImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val g2d = outputImage.createGraphics()

        // 填充白色背景
        g2d.color = Color.WHITE
        g2d.fillRect(0, 0, width, height)
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // 计算缩放后的尺寸
        val scaledWidth = (width * scale).toInt()
        val scaledHeight = (height * scale).toInt()

        // 计算居中位置
        val x = (width - scaledWidth) / 2
        val y = (height - scaledHeight) / 2

        // 绘制缩放后的图像
        g2d.drawImage(srcImage, x, y, scaledWidth, scaledHeight, null)
        g2d.dispose()

        return outputImage
    }
}