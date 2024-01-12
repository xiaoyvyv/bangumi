package com.xiaoyv.common.widget.state

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.GridLayoutManager
import com.xiaoyv.common.api.parser.entity.MediaStatsEntity
import com.xiaoyv.common.databinding.ViewChartGridStateBinding
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.tint
import com.xiaoyv.common.widget.scroll.AnimeRecyclerView
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.getDpx

/**
 * Class: [AnimeGridStateView]
 *
 * @author why
 * @since 1/12/24
 */
class AnimeGridStateView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : AnimeRecyclerView(context, attrs) {
    private val itemAdapter by lazy { ItemAdapter() }

    var data: List<MediaStatsEntity.GridState> = emptyList()
        set(value) {
            field = value
            itemAdapter.submitList(value)
        }

    init {
        isNestedScrollingEnabled = false
        overScrollMode = OVER_SCROLL_NEVER
        itemAnimator = null
        layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
        adapter = itemAdapter

        updatePadding(left = getDpx(8f), right = getDpx(8f))
    }

    private class ItemAdapter : BaseQuickDiffBindingAdapter<MediaStatsEntity.GridState,
            ViewChartGridStateBinding>(IdDiffItemCallback()) {

        override fun BaseQuickBindingHolder<ViewChartGridStateBinding>.converted(item: MediaStatsEntity.GridState) {
            binding.itemGridState.backgroundTintList = item.color.tint
            binding.tvName1.text = item.title
            binding.tvValue1.text = item.desc
        }
    }
}