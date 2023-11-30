package com.xiaoyv.bangumi.ui.media.detail.overview.binder

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.databinding.FragmentOverviewIndexBinding
import com.xiaoyv.bangumi.databinding.FragmentOverviewIndexItemBinding
import com.xiaoyv.bangumi.ui.media.detail.overview.OverviewAdapter
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.helper.RecyclerItemTouchedListener
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [OverviewIndexBinder]
 *
 * @author why
 * @since 11/30/23
 */
class OverviewIndexBinder(private val touchedListener: RecyclerItemTouchedListener) :
    BaseMultiItemAdapter.OnMultiItemAdapterListener<OverviewAdapter.OverviewItem, BaseQuickBindingHolder<FragmentOverviewIndexBinding>> {

    private val itemAdapter by lazy {
        ItemAdapter()
    }

    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentOverviewIndexBinding>,
        position: Int,
        item: OverviewAdapter.OverviewItem?
    ) {
        item ?: return
        holder.binding.rvIndex.adapter = itemAdapter
        holder.binding.rvIndex.addOnItemTouchListener(touchedListener)
        holder.binding.rvIndex.setInitialPrefetchItemCount(item.mediaDetailEntity.recommendIndex.size)
        itemAdapter.submitList(item.mediaDetailEntity.recommendIndex)
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ) = BaseQuickBindingHolder(
        FragmentOverviewIndexBinding.inflate(context.inflater, parent, false)
    )

    private class ItemAdapter :
        BaseQuickDiffBindingAdapter<MediaDetailEntity.MediaIndex, FragmentOverviewIndexItemBinding>(
            DiffItemCallback
        ) {
        override fun BaseQuickBindingHolder<FragmentOverviewIndexItemBinding>.converted(item: MediaDetailEntity.MediaIndex) {
            binding.ivAvatar.loadImageAnimate(item.userAvatar)
            binding.tvName.text = item.title
            binding.tvJob.text = item.userName
        }

        private object DiffItemCallback :
            DiffUtil.ItemCallback<MediaDetailEntity.MediaIndex>() {
            override fun areItemsTheSame(
                oldItem: MediaDetailEntity.MediaIndex,
                newItem: MediaDetailEntity.MediaIndex
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: MediaDetailEntity.MediaIndex,
                newItem: MediaDetailEntity.MediaIndex
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}