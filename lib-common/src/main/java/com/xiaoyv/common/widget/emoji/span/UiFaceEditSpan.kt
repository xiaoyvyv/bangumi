package com.xiaoyv.common.widget.emoji.span

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.drawable.Drawable
import android.text.style.DynamicDrawableSpan
import com.blankj.utilcode.util.ResourceUtils
import com.xiaoyv.common.R
import com.xiaoyv.common.widget.emoji.edit.UiFaceCache
import java.lang.ref.WeakReference

/**
 * 表情
 *
 * @author why
 */
class UiFaceEditSpan(
        private val mContext: Context,
        private val mResourceId: Int,
        private val mSize: Int,
        private val mTextSize: Int,
) : DynamicDrawableSpan(ALIGN_BASELINE) {
    private var mHeight: Int
    private var mWidth: Int
    private var mTop = 0
    private var mDrawable: Drawable? = null
    private var mDrawableRef: WeakReference<Drawable?>? = null

    /**
     * 手动偏移值
     */
    private var mTranslateY = 0

    init {
        mHeight = mSize
        mWidth = mHeight
    }

    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: FontMetricsInt?): Int {
        // fm 以 Paint 的 fm 为基准，避免 Span 改变了 fm 导致文字行高变化
        val d = getCachedDrawable()
        val rect = d.bounds
        if (fm != null) {
            val pfm = paint.fontMetricsInt
            // 保持与画笔的fm相同
            fm.ascent = pfm.ascent
            fm.descent = pfm.descent
            fm.top = pfm.top
            fm.bottom = pfm.bottom
        }
        return rect.right
    }


    override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        val b = getCachedDrawable()
        val count = canvas.save()

        // 因为 TextView 加了 lineSpacing 之后会导致这里的 bottom、top 参数与单行情况不一样
        // 所以不用 bottom、top，而使用 fontMetrics 的高度来计算
        val fontMatricesTop = y + paint.fontMetricsInt.top
        val fontMatricesBottom = fontMatricesTop + (paint.fontMetricsInt.bottom - paint.fontMetricsInt.top)
        var transY = fontMatricesTop + (fontMatricesBottom - fontMatricesTop) / 2 - (b.bounds.bottom - b.bounds.top) / 2 - mTop
        transY += mTranslateY
        canvas.translate(x, transY.toFloat())
        b.draw(canvas)
        canvas.restoreToCount(count)
    }

    private fun getCachedDrawable(): Drawable {
        if (mDrawableRef == null || mDrawableRef!!.get() == null) {
            mDrawableRef = WeakReference(drawable)
        }
        return mDrawableRef!!.get()!!
    }

    override fun getDrawable(): Drawable {
        if (mDrawable == null) {
            try {
                mDrawable = UiFaceCache.getInstance().getDrawable(mContext, mResourceId)
                if (mDrawable != null) {
                    mHeight = mSize
                    mWidth = mHeight * mDrawable!!.intrinsicWidth / mDrawable!!.intrinsicHeight
                    mTop = (mTextSize - mHeight) / 2
                    mDrawable!!.setBounds(0, mTop, mWidth, mTop + mHeight)
                }
            } catch (e: Exception) {
                mDrawable = ResourceUtils.getDrawable(R.drawable.ic_emoji_blank)
            }
        }
        return mDrawable!!
    }

    fun setTranslateY(translateY: Int) {
        mTranslateY = translateY
    }
}