package com.xiaoyv.bangumi.shared.core.bmo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import androidx.core.graphics.createBitmap
import java.io.ByteArrayOutputStream
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

actual object BmoImageCompositor {
    actual fun composite(
        layers: List<Pair<BmoResolvedItem, ByteArray>>,
        canvasWidth: Int,
        canvasHeight: Int
    ): ByteArray? {
        val bitmap = createBitmap(canvasWidth, canvasHeight)
        val canvas = Canvas(bitmap)

        for ((item, pngBytes) in layers) {
            val srcBitmap = try {
                BitmapFactory.decodeByteArray(pngBytes, 0, pngBytes.size)
            } catch (_: Throwable) {
                null
            } ?: continue

            val mods = item.modifiers
            val matrix = Matrix()

            // 将原点移至图片中心
            matrix.postTranslate(-srcBitmap.width / 2f, -srcBitmap.height / 2f)

            // 填充画布并应用缩放与翻转
            val scaleToFillX = canvasWidth.toFloat() / srcBitmap.width
            val scaleToFillY = canvasHeight.toFloat() / srcBitmap.height
            val sx = (if (mods.flipH) -1f else 1f) * mods.scaleX * scaleToFillX
            val sy = (if (mods.flipV) -1f else 1f) * mods.scaleY * scaleToFillY
            matrix.postScale(sx, sy)

            // 应用旋转
            if (mods.rotation != 0f) {
                matrix.postRotate(mods.rotation)
            }

            // 平移至画布目标中心
            val cx = canvasWidth / 2f + mods.x
            val cy = canvasHeight / 2f + mods.y
            matrix.postTranslate(cx, cy)

            // 设置画笔与 HSL 色彩滤镜
            val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            val colorFilter = buildHslColorFilter(mods.hue, mods.saturation, mods.lightness)
            if (colorFilter != null) {
                paint.colorFilter = colorFilter
            }

            canvas.drawBitmap(srcBitmap, matrix, paint)
            srcBitmap.recycle()
        }

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        bitmap.recycle()
        return outputStream.toByteArray()
    }

    private fun buildHslColorFilter(hue: Float?, saturation: Float?, lightness: Float?): ColorFilter? {
        if (hue == null && saturation == null && lightness == null) return null

        val cm = ColorMatrix()

        // 饱和度调整
        if (saturation != null) {
            val s = (1f + saturation / 100f).coerceAtLeast(0f)
            val satCm = ColorMatrix()
            satCm.setSaturation(s)
            cm.postConcat(satCm)
        }

        // 亮度调整
        if (lightness != null) {
            val offset = (lightness / 100f * 255f).coerceIn(-255f, 255f)
            val lightCm = ColorMatrix(
                floatArrayOf(
                    1f, 0f, 0f, 0f, offset,
                    0f, 1f, 0f, 0f, offset,
                    0f, 0f, 1f, 0f, offset,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            cm.postConcat(lightCm)
        }

        // 色相旋转
        if (hue != null && hue != 0f) {
            val rad = hue * (PI / 180.0)
            val cos = cos(rad).toFloat()
            val sin = sin(rad).toFloat()
            val lumR = 0.213f
            val lumG = 0.715f
            val lumB = 0.072f
            val hueCm = ColorMatrix(
                floatArrayOf(
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
            )
            cm.postConcat(hueCm)
        }

        return ColorMatrixColorFilter(cm)
    }
}
