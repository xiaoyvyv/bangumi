package com.xiaoyv.common.helper.callback

import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

/**
 * Class: [RecyclerItemTouchedListener]
 *
 * @author why
 * @since 11/23/23
 */
class RecyclerItemTouchedListener(val isUserInputEnabled: (Boolean) -> Unit) :
    RecyclerView.OnItemTouchListener {
    private val resetHandler = {
        isUserInputEnabled(true)
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        when (e.action) {
            MotionEvent.ACTION_MOVE -> {
                rv.removeCallbacks(resetHandler)
                isUserInputEnabled(false)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                rv.removeCallbacks(resetHandler)
                rv.postDelayed(resetHandler, 300)
            }
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

    }
}