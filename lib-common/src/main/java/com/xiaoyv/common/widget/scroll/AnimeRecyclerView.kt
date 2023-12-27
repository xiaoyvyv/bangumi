package com.xiaoyv.common.widget.scroll

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xiaoyv.common.helper.FixHelper
import com.xiaoyv.widget.kts.isDestroyed

/**
 * Class: [AnimeRecyclerView]
 *
 * @author why
 * @since 11/23/23
 */
open class AnimeRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : RecyclerView(context, attrs) {
    private var scrolling = false
    private var itemViewCacheSize = 20
    private var maxRecycledViews = 10

    init {
        FixHelper.fuckMiuiOverScroller(this)
        setItemViewCacheSize(itemViewCacheSize)
        setMaxRecycledViews(0, maxRecycledViews)
        addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == SCROLL_STATE_SETTLING || newState == SCROLL_STATE_DRAGGING) {
                    if (!scrolling) {
                        if (!context.isDestroyed()) Glide.with(context).pauseRequests()
                    }
                    scrolling = true
                } else if (newState == SCROLL_STATE_IDLE) {
                    if (!context.isDestroyed()) Glide.with(context).resumeRequests()
                    scrolling = false
                }
            }
        })
    }

    final override fun setItemViewCacheSize(size: Int) {
        super.setItemViewCacheSize(size)
    }

    final override fun addOnScrollListener(listener: OnScrollListener) {
        super.addOnScrollListener(listener)
    }

    override fun setLayoutManager(layout: LayoutManager?) {
        super.setLayoutManager(layout)
        setInitialPrefetchItemCount(3)
    }

    fun setInitialPrefetchItemCount(count: Int) {
        (layoutManager as? LinearLayoutManager)?.apply {
            isItemPrefetchEnabled = true
            initialPrefetchItemCount = count
        }
    }

    fun setMaxRecycledViews(viewType: Int, max: Int) {
        recycledViewPool.setMaxRecycledViews(viewType, max)
    }
}