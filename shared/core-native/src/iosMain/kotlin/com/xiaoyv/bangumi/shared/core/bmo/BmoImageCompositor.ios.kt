package com.xiaoyv.bangumi.shared.core.bmo

import org.jetbrains.skia.ColorFilter
import org.jetbrains.skia.ColorMatrix
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Surface
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

actual object BmoImageCompositor {
    actual fun composite(
        layers: List<Pair<BmoResolvedItem, ByteArray>>,
        canvasWidth: Int,
        canvasHeight: Int
    ): ByteArray? {
        val surface = Surface.makeRasterN32Premul(canvasWidth, canvasHeight)
        val canvas = surface.canvas

        for ((item, pngBytes) in layers) {
            val skiaImage = try {
                Image.makeFromEncoded(pngBytes)
            } catch (_: Throwable) {
                null
            } ?: continue

            val mods = item.modifiers

            canvas.save()

            // 平移至画布目标中心
            val cx = canvasWidth / 2f + mods.x
            val cy = canvasHeight / 2f + mods.y
            canvas.translate(cx, cy)

            // 应用旋转
            if (mods.rotation != 0f) {
                canvas.rotate(mods.rotation)
            }

            // 填充画布并应用缩放与翻转
            val scaleToFillX = canvasWidth.toFloat() / skiaImage.width
            val scaleToFillY = canvasHeight.toFloat() / skiaImage.height
            val sx = (if (mods.flipH) -1f else 1f) * mods.scaleX * scaleToFillX
            val sy = (if (mods.flipV) -1f else 1f) * mods.scaleY * scaleToFillY
            if (sx != 0f || sy != 0f) {
                canvas.scale(sx, sy)
            }

            // 设置画笔与 HSL 色彩滤镜
            val paint = Paint()
            val colorFilter = buildHslColorFilter(mods.hue, mods.saturation, mods.lightness)
            if (colorFilter != null) {
                paint.colorFilter = colorFilter
            }

            // 以图片中心对齐绘制
            val drawX = -skiaImage.width / 2f
            val drawY = -skiaImage.height / 2f
            canvas.drawImage(skiaImage, drawX, drawY, paint)

            canvas.restore()
        }

        val snapshot = surface.makeImageSnapshot()
        val pngData = snapshot.encodeToData(EncodedImageFormat.PNG) ?: return null
        return pngData.bytes
    }

    private fun buildHslColorFilter(hue: Float?, saturation: Float?, lightness: Float?): ColorFilter? {
        if (hue == null && saturation == null && lightness == null) return null

        var mat = floatArrayOf(
            1f, 0f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f, 0f,
            0f, 0f, 1f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )

        // 饱和度调整
        if (saturation != null) {
            val s = (1f + saturation / 100f).coerceAtLeast(0f)
            val invS = 1f - s
            val R = 0.2126f * invS
            val G = 0.7152f * invS
            val B = 0.0722f * invS
            val satMat = floatArrayOf(
                R + s, G, B, 0f, 0f,
                R, G + s, B, 0f, 0f,
                R, G, B + s, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
            mat = multiplyColorMatrix(mat, satMat)
        }

        // 亮度调整
        if (lightness != null) {
            val offset = (lightness / 100f * 255f).coerceIn(-255f, 255f)
            val lightMat = floatArrayOf(
                1f, 0f, 0f, 0f, offset,
                0f, 1f, 0f, 0f, offset,
                0f, 0f, 1f, 0f, offset,
                0f, 0f, 0f, 1f, 0f
            )
            mat = multiplyColorMatrix(mat, lightMat)
        }

        // 色相旋转
        if (hue != null && hue != 0f) {
            val rad = hue * (PI / 180.0)
            val cos = cos(rad).toFloat()
            val sin = sin(rad).toFloat()
            val lumR = 0.213f
            val lumG = 0.715f
            val lumB = 0.072f
            val hueMat = floatArrayOf(
                lumR + cos * (1f - lumR) + sin * (-lumR),
                lumG + cos * (-lumG) + sin * (-lumG),
                lumB + cos * (-lumB) + sin * (1f - lumB),
                0f, 0f,

                lumR + cos * (-lumR) + sin * 0.143f,
                lumG + cos * (1f - lumG) + sin * 0.140f,
                lumB + cos * (-lumB) + sin * (-0.283f),
                0f, 0f,

                lumR + cos * (-lumR) + sin * (-(1f - lumR)),
                lumG + cos * (-lumG) + sin * lumG,
                lumB + cos * (1f - lumB) + sin * lumB,
                0f, 0f,

                0f, 0f, 0f, 1f, 0f
            )
            mat = multiplyColorMatrix(mat, hueMat)
        }

        return ColorFilter.makeMatrix(ColorMatrix(mat))
    }

    private fun multiplyColorMatrix(a: FloatArray, b: FloatArray): FloatArray {
        val result = FloatArray(20)
        for (row in 0 until 4) {
            for (col in 0 until 4) {
                var sum = 0f
                for (k in 0 until 4) {
                    sum += b[row * 5 + k] * a[k * 5 + col]
                }
                result[row * 5 + col] = sum
            }
            var offsetSum = b[row * 5 + 4]
            for (k in 0 until 4) {
                offsetSum += b[row * 5 + k] * a[k * 5 + 4]
            }
            result[row * 5 + 4] = offsetSum
        }
        return result
    }
}
