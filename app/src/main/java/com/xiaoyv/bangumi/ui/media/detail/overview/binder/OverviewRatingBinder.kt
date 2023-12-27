package com.xiaoyv.bangumi.ui.media.detail.overview.binder

import android.content.Context
import android.view.ViewGroup
import com.blankj.utilcode.util.SpanUtils
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.databinding.FragmentOverviewRatingBinding
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.config.bean.AdapterTypeItem
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.forceCast
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.kts.dpi
import com.xiaoyv.widget.kts.getAttrColor

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
        item: AdapterTypeItem?,
    ) {
        item ?: return
        val context = holder.binding.root.context
        holder.binding.tvSection.title = item.title
        holder.binding.tvSection.more = "查看评分 >>"
        item.entity.forceCast<MediaDetailEntity>().apply {
            val total = rating.ratingDetail.sumOf { item -> item.count.toLong() }
            holder.binding.tvScore.text = String.format("%.1f", rating.globalRating)

            if (rating.globalRank != 0) {
                holder.binding.tvScoreTip.text =
                    String.format("#%d %s", rating.globalRank, rating.description)
            } else {
                holder.binding.tvScoreTip.text = String.format("%s", rating.description)
            }

            holder.binding.clLine.setData(rating.ratingDetail, total, false)
            holder.binding.tvRatingStandardDeviation.text =
                String.format("标准差：%.2f", rating.standardDeviation)

            if (friendRating.count > 0) {
                SpanUtils.with(holder.binding.tvFriendRating)
                    .append("好友评价：")
                    .append(String.format("%.1f", friendRating.score))
                    .setForegroundColor(context.getAttrColor(GoogleAttr.colorPrimary))
                    .appendSpace(6.dpi)
                    .append(friendRating.desc)
                    .appendSpace(6.dpi)
                    .append(String.format("%d 人评分", friendRating.count))
                    .create()
            } else {
                holder.binding.tvFriendRating.text = null
            }
        }
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
    ) = BaseQuickBindingHolder(
        FragmentOverviewRatingBinding.inflate(context.inflater, parent, false)
    )
}