package com.xiaoyv.bangumi.ui.media.detail.overview.binder

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.databinding.FragmentOverviewSaveBinding
import com.xiaoyv.bangumi.ui.media.detail.overview.OverviewAdapter
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.widget.binder.BaseQuickBindingHolder

/**
 * Class: [OverviewSaveBinder]
 *
 * @author why
 * @since 11/30/23
 */
class OverviewSaveBinder :
    BaseMultiItemAdapter.OnMultiItemAdapterListener<OverviewAdapter.OverviewItem, BaseQuickBindingHolder<FragmentOverviewSaveBinding>> {
    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentOverviewSaveBinding>,
        position: Int,
        item: OverviewAdapter.OverviewItem?
    ) {
        item ?: return
        holder.binding.tvSave.text = item.mediaDetailEntity.collectState
        holder.binding.tvSaveSummary.text = String.format(
            "%d人想看 / %d人看过 / %d人在看 / %d人搁置 / %d人抛弃",
            item.mediaDetailEntity.countWish,
            item.mediaDetailEntity.countCollect,
            item.mediaDetailEntity.countDoing,
            item.mediaDetailEntity.countOnHold,
            item.mediaDetailEntity.countDropped
        )
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ) = BaseQuickBindingHolder(
        FragmentOverviewSaveBinding.inflate(context.inflater, parent, false)
    )
}