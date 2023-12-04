package com.xiaoyv.bangumi.ui.media.detail.overview.binder

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentOverviewReviewBinding
import com.xiaoyv.bangumi.ui.media.detail.overview.OverviewAdapter
import com.xiaoyv.bangumi.ui.media.detail.review.MediaReviewAdapter
import com.xiaoyv.common.api.parser.entity.MediaCommentEntity
import com.xiaoyv.common.api.parser.entity.MediaReviewEntity
import com.xiaoyv.common.helper.RecyclerItemTouchedListener
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickBindingHolder

/**
 * Class: [OverviewReviewBinder]
 *
 * @author why
 * @since 11/30/23
 */
class OverviewReviewBinder(
    private val touchedListener: RecyclerItemTouchedListener,
    private val clickItemListener: (MediaReviewEntity) -> Unit,
    private val clickUserListener: (MediaReviewEntity) -> Unit
) : BaseMultiItemAdapter.OnMultiItemAdapterListener<OverviewAdapter.OverviewItem, BaseQuickBindingHolder<FragmentOverviewReviewBinding>> {

    private val itemAdapter by lazy { MediaReviewAdapter() }

    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentOverviewReviewBinding>,
        position: Int,
        item: OverviewAdapter.OverviewItem?
    ) {
        item ?: return
        holder.binding.rvReview.adapter = itemAdapter
        holder.binding.rvReview.addOnItemTouchListener(touchedListener)
        holder.binding.rvReview.setInitialPrefetchItemCount(item.mediaDetailEntity.reviews.size)
        itemAdapter.submitList(item.mediaDetailEntity.reviews)
        itemAdapter.setOnDebouncedChildClickListener(R.id.item_review, block = clickItemListener)
        itemAdapter.setOnDebouncedChildClickListener(R.id.iv_avatar, block = clickUserListener)
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ) = BaseQuickBindingHolder(
        FragmentOverviewReviewBinding.inflate(context.inflater, parent, false)
    )
}