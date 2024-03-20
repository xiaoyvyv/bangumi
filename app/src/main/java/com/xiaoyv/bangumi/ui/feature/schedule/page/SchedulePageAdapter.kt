package com.xiaoyv.bangumi.ui.feature.schedule.page

import androidx.core.view.isVisible
import com.xiaoyv.bangumi.databinding.FragmentSchedulePageItemBinding
import com.xiaoyv.common.api.response.api.ApiCalendarEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.IMAGE_HOLDER_3X4
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [SchedulePageAdapter]
 *
 * @author why
 * @since 3/20/24
 */
class SchedulePageAdapter : BaseQuickDiffBindingAdapter<ApiCalendarEntity.MediaItem,
        FragmentSchedulePageItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<FragmentSchedulePageItemBinding>.converted(item: ApiCalendarEntity.MediaItem) {
        val score = item.rating?.score ?: 0.0
        val epsCount = item.epsCount

        binding.ivCover.loadImageAnimate(item.cover, holderType = IMAGE_HOLDER_3X4)
        binding.tvTitle.text = item.nameCn.orEmpty().ifBlank { item.name }

        binding.tvSummary.text = item.summary.orEmpty()
        binding.tvSummary.isVisible = item.summary.orEmpty().isNotBlank()

        binding.tvRank.text = if (score == 0.0) "暂无" else score.toString()

        binding.tvEps.text = epsCount.toString()
        binding.tvEps.isVisible = epsCount != 0
    }
}