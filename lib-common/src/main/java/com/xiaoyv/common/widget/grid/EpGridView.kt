package com.xiaoyv.common.widget.grid

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ScreenUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.xiaoyv.common.R
import com.xiaoyv.common.api.parser.entity.MediaChapterEntity
import com.xiaoyv.common.config.annotation.InterestType
import com.xiaoyv.common.widget.scroll.AnimeRecyclerView

/**
 * Class: [EpGridView]
 *
 * @author why
 * @since 12/22/23
 */
class EpGridView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : AnimeRecyclerView(context, attrs) {
    private val gridHorAdapter by lazy { EpGridHorItemAdapter() }
    private val gridVerAdapter by lazy { EpGridVerItemAdapter() }


    private val horizontalManager by lazy {
        GridLayoutManager(context, SPAN_COUNT_HORIZONTAL, LinearLayoutManager.HORIZONTAL, false)
    }

    private val verticalManager by lazy {
        GridLayoutManager(context, SPAN_COUNT_VERTICAL, LinearLayoutManager.VERTICAL, false)
    }

    init {
        hasFixedSize()
        itemAnimator = null
    }

    /**
     * 设置格子数据
     */
    fun fillMediaChapters(
        list: List<MediaChapterEntity>,
        autoScrollWatched: Boolean,
        listener: BaseQuickAdapter.OnItemChildClickListener<MediaChapterEntity>,
    ) {
        if (isHorizontalGrid(list.size)) {
            if (layoutManager == null) layoutManager = horizontalManager
            if (adapter == null) adapter = gridHorAdapter
            gridHorAdapter.addOnItemChildClickListener(R.id.item_ep, listener)
            gridHorAdapter.submitList(list) {
                if (autoScrollWatched) scrollToWatched()
            }
        } else {
            if (layoutManager == null) layoutManager = verticalManager
            if (adapter == null) adapter = gridVerAdapter
            gridVerAdapter.addOnItemChildClickListener(R.id.item_ep, listener)
            gridVerAdapter.submitList(list)
        }
    }

    /**
     * 滑动到第一个不是看过的格子
     */
    fun scrollToWatched() {
        val targetIndex = gridHorAdapter.items.indexOfLast {
            it.collectType == InterestType.TYPE_COLLECT
        }

        if (targetIndex != -1) {
            horizontalManager.scrollToPositionWithOffset(
                targetIndex, ScreenUtils.getScreenWidth() / 3
            )
        }
    }

    companion object {
        const val SPAN_COUNT_HORIZONTAL = 5
        const val SPAN_COUNT_VERTICAL = 8

        fun isHorizontalGrid(size: Int): Boolean {
            return size > SPAN_COUNT_VERTICAL * SPAN_COUNT_HORIZONTAL
        }
    }
}