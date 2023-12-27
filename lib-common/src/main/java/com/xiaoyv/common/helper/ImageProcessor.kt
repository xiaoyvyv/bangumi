package com.xiaoyv.common.helper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import java.io.File

class ImageProcessor {

    fun cropImage(imageFile: File, box: List<Double>): Bitmap {
        // 获取原始图片的尺寸信息
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imageFile.absolutePath, options)
        val originalWidth = options.outWidth
        val originalHeight = options.outHeight

        // 计算矩形的坐标
        val left = (box[0] * originalWidth).toInt()
        val top = (box[1] * originalHeight).toInt()
        val right = (box[2] * originalWidth).toInt()
        val bottom = (box[3] * originalHeight).toInt()

        // 限制图片的最大尺寸
        val maxWidth = 1024
        val maxHeight = 1024
        val sampleSize = calculateInSampleSize(originalWidth, originalHeight, maxWidth, maxHeight)

        // 设置解码参数
        val decodeOptions = BitmapFactory.Options()
        decodeOptions.inSampleSize = sampleSize

        // 创建一个新的Bitmap，用于存储截取后的图像
        val croppedBitmap = Bitmap.createBitmap(
            right - left,
            bottom - top,
            Bitmap.Config.ARGB_8888
        )

        // 创建一个Canvas，并将原始图像绘制到新的Bitmap上
        val canvas = Canvas(croppedBitmap)
        val sourceRect = Rect(left, top, right, bottom)
        val destinationRect = Rect(0, 0, croppedBitmap.width, croppedBitmap.height)
        BitmapFactory.decodeFile(imageFile.absolutePath, decodeOptions)?.let {
            canvas.drawBitmap(it, sourceRect, destinationRect, null)
        }

        // 返回截取后的Bitmap
        return croppedBitmap
    }

    private fun calculateInSampleSize(
        width: Int,
        height: Int,
        maxWidth: Int,
        maxHeight: Int
    ): Int {
        var inSampleSize = 1
        if (height > maxHeight || width > maxWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while ((halfHeight / inSampleSize) >= maxHeight
                && (halfWidth / inSampleSize) >= maxWidth
            ) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}