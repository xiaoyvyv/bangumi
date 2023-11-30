package com.xiaoyv.bangumi.ui.media.detail.overview.binder

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.databinding.FragmentOverviewCommentBinding
import com.xiaoyv.bangumi.ui.media.detail.comments.MediaCommentAdapter
import com.xiaoyv.bangumi.ui.media.detail.overview.OverviewAdapter
import com.xiaoyv.common.helper.RecyclerItemTouchedListener
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.widget.binder.BaseQuickBindingHolder

/**
 * Class: [OverviewCommentBinder]
 *
 * @author why
 * @since 11/30/23
 */
class OverviewCommentBinder(private val touchedListener: RecyclerItemTouchedListener) :
    BaseMultiItemAdapter.OnMultiItemAdapterListener<OverviewAdapter.OverviewItem, BaseQuickBindingHolder<FragmentOverviewCommentBinding>> {
    private val itemAdapter by lazy { MediaCommentAdapter() }

    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentOverviewCommentBinding>,
        position: Int,
        item: OverviewAdapter.OverviewItem?
    ) {
        item ?: return
        holder.binding.rvComment.adapter = itemAdapter
        holder.binding.rvComment.addOnItemTouchListener(touchedListener)
        itemAdapter.submitList(item.mediaDetailEntity.comments)
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ) = BaseQuickBindingHolder(
        FragmentOverviewCommentBinding.inflate(context.inflater, parent, false)
    )
}