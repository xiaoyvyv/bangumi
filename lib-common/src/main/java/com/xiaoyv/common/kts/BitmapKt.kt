package com.xiaoyv.common.kts

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF


fun Bitmap.centerCropBitmap(
    containerWidth: Int,
    containerHeight: Int,
): Bitmap {
    // 计算裁剪后的目标矩形区域
    val sourceRect = Rect(0, 0, width, height)
    val destRect = getCenterCropRect(sourceRect, containerWidth, containerHeight)

    return Bitmap.createBitmap(containerWidth, containerHeight, Bitmap.Config.ARGB_8888).apply {
        with(Canvas(this)) {
            drawBitmap(this@centerCropBitmap, sourceRect, destRect, null)
        }
    }
}

fun getCenterCropRect(sourceRect: Rect, containerWidth: Int, containerHeight: Int): RectF {
    val sourceAspectRatio = sourceRect.width() / sourceRect.height().toFloat()
    val containerAspectRatio = containerWidth.toFloat() / containerHeight.toFloat()

    val scale: Float
    val scaledWidth: Float
    val scaledHeight: Float

    if (sourceAspectRatio > containerAspectRatio) {
        // 如果源图宽高比大于容器宽高比，以高度为基准进行缩放
        scale = containerHeight.toFloat() / sourceRect.height()
        scaledWidth = scale * sourceRect.width()
        scaledHeight = containerHeight.toFloat()
    } else {
        // 如果源图宽高比小于或等于容器宽高比，以宽度为基准进行缩放
        scale = containerWidth.toFloat() / sourceRect.width()
        scaledWidth = containerWidth.toFloat()
        scaledHeight = scale * sourceRect.height()
    }

    // 计算裁剪后的目标矩形区域
    val left = (containerWidth - scaledWidth) / 2
    val top = (containerHeight - scaledHeight) / 2
    val right = left + scaledWidth
    val bottom = top + scaledHeight

    return RectF(left, top, right, bottom)
}


fun Bitmap.roundedCorner(radius: Float): Bitmap {
    val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(result)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
    val path = Path()
    path.addRoundRect(rect, radius, radius, Path.Direction.CW)
    canvas.clipPath(path)
    canvas.drawBitmap(this, 0f, 0f, paint)
    return result
}