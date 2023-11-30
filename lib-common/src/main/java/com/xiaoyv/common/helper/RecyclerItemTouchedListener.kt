package com.xiaoyv.common.helper

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
    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        when (e.action) {
            MotionEvent.ACTION_MOVE -> {
                isUserInputEnabled(false)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isUserInputEnabled(true)
            }
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

    }
}