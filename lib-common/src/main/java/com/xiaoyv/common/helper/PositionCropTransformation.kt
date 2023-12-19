package com.xiaoyv.common.helper

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import androidx.annotation.FloatRange
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.PAINT_FLAGS
import java.nio.charset.Charset
import java.security.MessageDigest

class PositionCropTransformation(
    @FloatRange(from = 0.0, to = 1.0) val xPercentage: Float = 0.0f,
    @FloatRange(from = 0.0, to = 1.0) val yPercentage: Float = 0.0f,
) : BitmapTransformation() {

    companion object {
        private const val ID = "com.testbook.tbapp.base.utils.TopRightCropTransformation"
        private val idBytes = ID.toByteArray(Charset.forName("UTF-8"))
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(idBytes)
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int,
    ): Bitmap? {
        val toReuse = pool[outWidth, outHeight, toTransform.config]
        val transformed = crop(toReuse, toTransform, outWidth, outHeight, xPercentage, yPercentage)
        if (toReuse != transformed) {
            pool.put(toReuse)
        }
        return transformed
    }

    /**
     * A potentially expensive operation to crop the given Bitmap so that it fills the given dimensions. This operation
     * is significantly less expensive in terms of memory if a mutable Bitmap with the given dimensions is passed in
     * as well.
     *
     * @param recycled A mutable Bitmap with dimensions width and height that we can load the cropped portion of toCrop
     *                 into.
     * @param toCrop The Bitmap to resize.
     * @param width The width in pixels of the final Bitmap.
     * @param height The height in pixels of the final Bitmap.
     * @param xPercentage The horizontal percentage of the crop. 0.0f => left, 0.5f => center, 1.0f => right or anything in between 0 and 1
     * @param yPercentage The vertical percentage of the crop. 0.0f => top, 0.5f => center, 1.0f => bottom or anything in between 0 and 1
     * @return The resized Bitmap (will be recycled if recycled is not null).
     */
    private fun crop(
        recycled: Bitmap?,
        toCrop: Bitmap?,
        width: Int,
        height: Int,
        xPercentage: Float,
        yPercentage: Float,
    ): Bitmap? {
        if (toCrop == null) {
            return null
        } else if (toCrop.width == width && toCrop.height == height) {
            return toCrop
        }
        // From ImageView/Bitmap.createScaledBitmap.
        val scale: Float
        var dx = 0f
        var dy = 0f
        val m = Matrix()
        if (toCrop.width * height > width * toCrop.height) {
            scale = height.toFloat() / toCrop.height.toFloat()
            dx = width - toCrop.width * scale
            dx *= xPercentage
        } else {
            scale = width.toFloat() / toCrop.width.toFloat()
            dy = height - toCrop.height * scale
            dy *= yPercentage
        }
        m.setScale(scale, scale)
        m.postTranslate((dx + 0.5f), (dy + 0.5f))
        val result: Bitmap = recycled ?: Bitmap.createBitmap(width, height, getSafeConfig(toCrop)!!)

        // We don't add or remove alpha, so keep the alpha setting of the Bitmap we were given.
        TransformationUtils.setAlpha(toCrop, result)
        val canvas = Canvas(result)
        val paint = Paint(PAINT_FLAGS)
        canvas.drawBitmap(toCrop, m, paint)
        return result
    }

    private fun getSafeConfig(bitmap: Bitmap): Bitmap.Config {
        return bitmap.config
    }
}