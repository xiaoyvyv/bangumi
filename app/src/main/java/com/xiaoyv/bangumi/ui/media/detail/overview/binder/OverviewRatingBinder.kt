package com.xiaoyv.bangumi.ui.media.detail.overview.binder

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.databinding.FragmentOverviewRatingBinding
import com.xiaoyv.bangumi.ui.media.detail.overview.OverviewAdapter
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.widget.binder.BaseQuickBindingHolder

/**
 * Class: [OverviewRatingBinder]
 *
 * @author why
 * @since 11/30/23
 */
class OverviewRatingBinder :
    BaseMultiItemAdapter.OnMultiItemAdapterListener<OverviewAdapter.OverviewItem, BaseQuickBindingHolder<FragmentOverviewRatingBinding>> {
    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentOverviewRatingBinding>,
        position: Int,
        item: OverviewAdapter.OverviewItem?
    ) {
        item ?: return
        holder.binding.tvScore.text =
            String.format("%.1f", item.mediaDetailEntity.rating.globalRating)
        if (item.mediaDetailEntity.rating.globalRank != 0) {
            holder.binding.tvScoreTip.text = String.format(
                "#%d %s",
                item.mediaDetailEntity.rating.globalRank,
                item.mediaDetailEntity.rating.description
            )
        } else {
            holder.binding.tvScoreTip.text = String.format(
                "%s",
                item.mediaDetailEntity.rating.description
            )
        }

        holder.binding.tvRatingStandardDeviation.text =
            String.format("%.2f", item.mediaDetailEntity.rating.standardDeviation)
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ) = BaseQuickBindingHolder(
        FragmentOverviewRatingBinding.inflate(context.inflater, parent, false)
    )
}