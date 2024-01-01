package com.xiaoyv.bangumi.ui.media.detail.overview.binder

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentOverviewCommentBinding
import com.xiaoyv.bangumi.ui.media.detail.comments.MediaCommentAdapter
import com.xiaoyv.common.api.parser.entity.MediaCommentEntity
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.config.bean.AdapterTypeItem
import com.xiaoyv.common.helper.callback.RecyclerItemTouchedListener
import com.xiaoyv.common.kts.forceCast
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickBindingHolder

/**
 * Class: [OverviewCommentBinder]
 *
 * @author why
 * @since 11/30/23
 */
class OverviewCommentBinder(
    private val touchedListener: RecyclerItemTouchedListener,
    private val clickItemListener: (MediaCommentEntity) -> Unit,
    private val clickUserListener: (MediaCommentEntity) -> Unit,
) : BaseMultiItemAdapter.OnMultiItemAdapterListener<AdapterTypeItem, BaseQuickBindingHolder<FragmentOverviewCommentBinding>> {

    private val itemAdapter by lazy {
        MediaCommentAdapter().apply {
            setOnDebouncedChildClickListener(R.id.item_comment, block = clickItemListener)
            setOnDebouncedChildClickListener(R.id.iv_avatar, block = clickUserListener)
        }
    }

    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentOverviewCommentBinding>,
        position: Int,
        item: AdapterTypeItem?
    ) {
        item ?: return
        holder.binding.sectionComment.title = item.title
        holder.binding.rvComment.adapter = itemAdapter
        holder.binding.rvComment.addOnItemTouchListener(touchedListener)

        item.entity.forceCast<MediaDetailEntity>().apply {
            holder.binding.rvComment.setInitialPrefetchItemCount(comments.size)
            itemAdapter.submitList(comments)
        }
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ) = BaseQuickBindingHolder(
        FragmentOverviewCommentBinding.inflate(context.inflater, parent, false)
    )
}