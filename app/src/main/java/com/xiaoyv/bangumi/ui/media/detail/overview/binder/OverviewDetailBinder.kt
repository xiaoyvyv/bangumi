package com.xiaoyv.bangumi.ui.media.detail.overview.binder

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.databinding.FragmentOverviewDetailBinding
import com.xiaoyv.bangumi.ui.media.detail.overview.OverviewAdapter
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.widget.binder.BaseQuickBindingHolder

/**
 * Class: [OverviewDetailBinder]
 *
 * @author why
 * @since 11/30/23
 */
class OverviewDetailBinder :
    BaseMultiItemAdapter.OnMultiItemAdapterListener<OverviewAdapter.OverviewItem, BaseQuickBindingHolder<FragmentOverviewDetailBinding>> {
    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentOverviewDetailBinding>,
        position: Int,
        item: OverviewAdapter.OverviewItem?
    ) {
        item ?: return
        holder.binding.tvDetailContent.text = null
        item.mediaDetailEntity.infos.forEach {
            holder.binding.tvDetailContent.append(it)
            holder.binding.tvDetailContent.append("\n")
        }
        holder.binding.tvDetailContent.append("...")
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ) = BaseQuickBindingHolder(
        FragmentOverviewDetailBinding.inflate(context.inflater, parent, false)
    )
}