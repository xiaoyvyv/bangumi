package com.xiaoyv.bangumi.ui.media.detail.overview.binder

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.databinding.FragmentOverviewRatingBinding
import com.xiaoyv.bangumi.ui.media.detail.overview.OverviewAdapter
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.config.bean.AdapterTypeItem
import com.xiaoyv.common.kts.forceCast
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.widget.binder.BaseQuickBindingHolder

/**
 * Class: [OverviewRatingBinder]
 *
 * @author why
 * @since 11/30/23
 */
class OverviewRatingBinder :
    BaseMultiItemAdapter.OnMultiItemAdapterListener<AdapterTypeItem, BaseQuickBindingHolder<FragmentOverviewRatingBinding>> {
    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentOverviewRatingBinding>,
        position: Int,
        item: AdapterTypeItem?
    ) {
        item ?: return
        holder.binding.tvSection.title = item.title
        holder.binding.tvSection.more = "查看评分 >>"
        item.entity.forceCast<MediaDetailEntity>().rating.apply {
            val total = ratingDetail.sumOf { item -> item.count.toLong() }
            holder.binding.tvScore.text = String.format("%.1f", globalRating)

            if (globalRank != 0) {
                holder.binding.tvScoreTip.text = String.format("#%d %s", globalRank, description)
            } else {
                holder.binding.tvScoreTip.text = String.format("%s", description)
            }

            holder.binding.clLine.setData(ratingDetail, total, false)
            holder.binding.tvRatingStandardDeviation.text =
                String.format("标准差：%.2f", standardDeviation)
        }
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ) = BaseQuickBindingHolder(
        FragmentOverviewRatingBinding.inflate(context.inflater, parent, false)
    )
}