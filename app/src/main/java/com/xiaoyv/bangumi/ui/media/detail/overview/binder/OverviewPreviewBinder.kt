package com.xiaoyv.bangumi.ui.media.detail.overview.binder

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.databinding.FragmentOverviewPreviewBinding
import com.xiaoyv.bangumi.ui.media.detail.overview.OverviewAdapter
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.widget.binder.BaseQuickBindingHolder

/**
 * Class: [OverviewPreviewBinder]
 *
 * @author why
 * @since 11/30/23
 */
class OverviewPreviewBinder :
    BaseMultiItemAdapter.OnMultiItemAdapterListener<OverviewAdapter.Item, BaseQuickBindingHolder<FragmentOverviewPreviewBinding>> {
    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentOverviewPreviewBinding>,
        position: Int,
        item: OverviewAdapter.Item?
    ) {

    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ) = BaseQuickBindingHolder(
        FragmentOverviewPreviewBinding.inflate(context.inflater, parent, false)
    )
}