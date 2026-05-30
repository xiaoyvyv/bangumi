package com.xiaoyv.bangumi.shared.ui.component.image

import coil3.BitmapImage
import coil3.Image

/**
 * JVM 平台实现：从 Coil3 Image 中提取平均亮度
 *
 * 通过 Skia Bitmap 采样像素计算平均亮度
 */
actual fun computeAverageLuminance(image: Image): Float {
    val bitmap = (image as? BitmapImage)?.bitmap ?: return 0.3f

    val width = bitmap.width
    val height = bitmap.height
    if (width == 0 || height == 0) return 0.3f

    // 采样步长：最多采样 ~400 个像素点（20x20 网格）
    val stepX = maxOf(1, width / 20)
    val stepY = maxOf(1, height / 20)

    var totalLuminance = 0.0
    var count = 0

    var y = 0
    while (y < height) {
        var x = 0
        while (x < width) {
            val color = bitmap.getColor(x, y)
            val r = ((color shr 16) and 0xFF) / 255.0
            val g = ((color shr 8) and 0xFF) / 255.0
            val b = (color and 0xFF) / 255.0

            // 感知亮度公式
            totalLuminance += 0.299 * r + 0.587 * g + 0.114 * b
            count++

            x += stepX
        }
        y += stepY
    }

    return if (count > 0) (totalLuminance / count).toFloat() else 0.3f
}
