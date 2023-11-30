package com.xiaoyv.bangumi.ui.media.detail.overview.binder

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.databinding.FragmentOverviewBoardBinding
import com.xiaoyv.bangumi.ui.media.detail.board.MediaBoardAdapter
import com.xiaoyv.bangumi.ui.media.detail.overview.OverviewAdapter
import com.xiaoyv.common.helper.RecyclerItemTouchedListener
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.widget.binder.BaseQuickBindingHolder

/**
 * Class: [OverviewBoardBinder]
 *
 * @author why
 * @since 11/30/23
 */
class OverviewBoardBinder(private val touchedListener: RecyclerItemTouchedListener) :
    BaseMultiItemAdapter.OnMultiItemAdapterListener<OverviewAdapter.OverviewItem, BaseQuickBindingHolder<FragmentOverviewBoardBinding>> {

    private val itemAdapter by lazy { MediaBoardAdapter() }

    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentOverviewBoardBinding>,
        position: Int,
        item: OverviewAdapter.OverviewItem?
    ) {
        item ?: return
        holder.binding.rvBoard.adapter = itemAdapter
        holder.binding.rvBoard.addOnItemTouchListener(touchedListener)
        itemAdapter.submitList(item.mediaDetailEntity.boards)
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ) = BaseQuickBindingHolder(
        FragmentOverviewBoardBinding.inflate(context.inflater, parent, false)
    )
}