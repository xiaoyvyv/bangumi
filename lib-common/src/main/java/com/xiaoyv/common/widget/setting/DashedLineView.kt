package com.xiaoyv.common.widget.setting

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.widget.kts.getAttrColor
import com.xiaoyv.widget.kts.getDpx

class DashedLineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : View(context, attrs, defStyle) {

    private val path = Path()
    private val paint: Paint = Paint()

    init {
        val dpx = getDpx(4f).toFloat()
        paint.style = Paint.Style.STROKE
        paint.color = context.getAttrColor(GoogleAttr.colorPrimary)
        paint.pathEffect = android.graphics.DashPathEffect(floatArrayOf(dpx, dpx), 0f)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val startX = 0f
        val startY = height / 2f
        val stopX = width.toFloat()
        val stopY = height / 2f

        paint.strokeWidth = height.toFloat()
        path.moveTo(startX, startY)
        path.lineTo(stopX, stopY)

        canvas.drawPath(path, paint)
    }
}
