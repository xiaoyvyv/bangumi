package com.xiaoyv.bangumi.shared.ui.component.image

import android.graphics.Bitmap
import coil3.BitmapImage
import coil3.Image

/**
 * Android 平台实现：从 Coil3 Image 中提取平均亮度
 *
 * 通过采样像素计算平均亮度，避免遍历所有像素导致性能问题。
 * 注意：Coil 默认使用 HARDWARE Bitmap，不支持 getPixel()，需要先 copy 为软件 Bitmap。
 */
actual fun computeAverageLuminance(image: Image): Float {
    val originalBitmap = (image as? BitmapImage)?.bitmap ?: return 0.3f

    val width = originalBitmap.width
    val height = originalBitmap.height
    if (width == 0 || height == 0) return 0.3f

    // HARDWARE bitmap 不支持像素访问，需要复制为 ARGB_8888
    val bitmap = if (originalBitmap.config == Bitmap.Config.HARDWARE) {
        originalBitmap.copy(Bitmap.Config.ARGB_8888, false) ?: return 0.3f
    } else {
        originalBitmap
    }

    try {
        // 采样步长：最多采样 ~400 个像素点（20x20 网格）
        val stepX = maxOf(1, width / 20)
        val stepY = maxOf(1, height / 20)

        var totalLuminance = 0.0
        var count = 0

        var y = 0
        while (y < height) {
            var x = 0
            while (x < width) {
                val pixel = bitmap.getPixel(x, y)
                val r = (pixel shr 16 and 0xFF) / 255.0
                val g = (pixel shr 8 and 0xFF) / 255.0
                val b = (pixel and 0xFF) / 255.0

                // 感知亮度公式
                totalLuminance += 0.299 * r + 0.587 * g + 0.114 * b
                count++

                x += stepX
            }
            y += stepY
        }

        return if (count > 0) (totalLuminance / count).toFloat() else 0.3f
    } finally {
        if (bitmap !== originalBitmap) {
            bitmap.recycle()
        }
    }
}
