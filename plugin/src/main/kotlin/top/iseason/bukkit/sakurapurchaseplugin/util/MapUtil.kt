package top.iseason.bukkit.sakurapurchaseplugin.util

import com.github.johnnyjayjay.spigotmaps.MapBuilder
import com.github.johnnyjayjay.spigotmaps.rendering.ImageRenderer
import com.github.johnnyjayjay.spigotmaps.util.ImageTools
import org.bukkit.inventory.ItemStack
import top.iseason.bukkit.sakurapurchaseplugin.config.Config
import java.awt.image.BufferedImage
import javax.imageio.ImageIO


object MapUtil {
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
        return kotlin.runCatching {
            MapBuilder.create()
                .addRenderers(renderer)
                .build().createItemStack()
        }.getOrElse {
            it.printStackTrace()
            return null
        }
    }

}