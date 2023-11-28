package com.xiaoyv.common.widget.scroll

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xiaoyv.common.helper.FixHelper

/**
 * Class: [AnimeRecyclerView]
 *
 * @author why
 * @since 11/23/23
 */
class AnimeRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {
    private var scrolling = false
    private var itemViewCacheSize = 50
    private var maxRecycledViews = 20

    init {
        FixHelper.fuckMiuiOverScroller(this)
        setItemViewCacheSize(itemViewCacheSize)
        setMaxRecycledViews(0, maxRecycledViews)
        addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == SCROLL_STATE_SETTLING || newState == SCROLL_STATE_DRAGGING) {
                    if (!scrolling) {
                        Glide.with(context).pauseRequests()
                    }
                    scrolling = true
                } else if (newState == SCROLL_STATE_IDLE) {
                    Glide.with(context).resumeRequests()
                    scrolling = false
                }
            }
        })
    }

    override fun setLayoutManager(layout: LayoutManager?) {
        when (layout) {
            is LinearLayoutManager -> {
                layout.isItemPrefetchEnabled = true
                layout.initialPrefetchItemCount = 3
            }
        }
        super.setLayoutManager(layout)
    }

    fun setMaxRecycledViews(viewType: Int, max: Int) {
        recycledViewPool.setMaxRecycledViews(viewType, max)
    }
}