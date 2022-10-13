package top.iseason.bukkit.sakurapurchaseplugin.util

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage


object QRCodeUtil {
    private const val BLACK = -0x1000000
    private const val WHITE = -0x1

    /**
     * 生成二维码
     * @param content       扫码内容
     * @param qrWidth   二维码宽度
     * @param qrHeight   二维码高度
     * @param logoWidth     logo宽度
     * @param logoHeight     logo高度
     * @param logo      logo
     * @param qrColor   二维码颜色
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun generateQRcode(
        content: String?,
        qrWidth: Int, qrHeight: Int, logoWidth: Int, logoHeight: Int,
        logo: BufferedImage?, qrColor: Int
    ): BufferedImage {
        /** 定义Map集合封装二维码配置信息  */
        val hints: MutableMap<EncodeHintType, Any?> = HashMap()
        /** 设置二维码图片的内容编码  */
        hints[EncodeHintType.CHARACTER_SET] = "utf-8"
        /** 设置二维码图片的上、下、左、右间隙  */
        hints[EncodeHintType.MARGIN] = 1
        /** 设置二维码的纠错级别  */
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
        /**
         * 创建二维码字节转换对象
         * 第一个参数：二维码图片中的内容
         * 第二个参数：二维码格式器
         * 第三个参数：生成二维码图片的宽度
         * 第四个参数：生成二维码图片的高度
         * 第五个参数：生成二维码需要配置信息
         */
        val matrix = MultiFormatWriter().encode(
            content,
            BarcodeFormat.QR_CODE, qrWidth, qrHeight, hints
        )

        /** 获取二维码图片真正的宽度   */
        val matrix_width = matrix.width

        /** 获取二维码图片真正的高度   */
        val matrix_height = matrix.height

        /** 定义一张空白的缓冲流图片  */
        val image = BufferedImage(
            matrix_width, matrix_height,
            BufferedImage.TYPE_INT_RGB
        )
        /** 把二维码字节转换对象 转化 到缓冲流图片上  */
        for (x in 0 until matrix_width) {
            for (y in 0 until matrix_height) {
                /** 通过x、y坐标获取一点的颜色 true: 黑色  false: 白色  */
                val rgb = if (matrix[x, y]) qrColor else 0xFFFFFF
                image.setRGB(x, y, rgb)
            }
        }
        if (logo != null) {
            /** 获取缓冲流图片的画笔  */
            val g = image.graphics as Graphics2D
            /** 在二维码图片中间绘制logo  */
            g.drawImage(
                logo, (matrix_width - logoWidth) / 2,
                (matrix_height - logoHeight) / 2,
                logoWidth, logoHeight, null
            )
            /** 设置画笔的颜色  */
            g.color = Color.WHITE
            /** 设置画笔的粗细  */
            g.stroke = BasicStroke(5.0f)
            /** 设置消除锯齿  */
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            /** 绘制圆角矩形  */
            g.drawRoundRect(
                (matrix_width - logoWidth) / 2,
                (matrix_height - logoHeight) / 2,
                logoWidth, logoHeight, 10, 10
            )
        }
        return image
    }

}