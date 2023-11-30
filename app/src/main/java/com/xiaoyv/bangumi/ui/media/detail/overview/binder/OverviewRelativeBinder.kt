package com.xiaoyv.bangumi.ui.media.detail.overview.binder

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.databinding.FragmentOverviewRelativeBinding
import com.xiaoyv.bangumi.databinding.FragmentOverviewRelativeItemBinding
import com.xiaoyv.bangumi.ui.media.detail.overview.OverviewAdapter
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.helper.RecyclerItemTouchedListener
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [OverviewRelativeBinder]
 *
 * @author why
 * @since 11/30/23
 */
class OverviewRelativeBinder(private val touchedListener: RecyclerItemTouchedListener) :
    BaseMultiItemAdapter.OnMultiItemAdapterListener<OverviewAdapter.OverviewItem, BaseQuickBindingHolder<FragmentOverviewRelativeBinding>> {

    private val itemAdapter by lazy {
        ItemAdapter()
    }

    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentOverviewRelativeBinding>,
        position: Int,
        item: OverviewAdapter.OverviewItem?
    ) {
        item ?: return
        holder.binding.rvRelative.adapter = itemAdapter
        holder.binding.rvRelative.addOnItemTouchListener(touchedListener)
        itemAdapter.submitList(item.mediaDetailEntity.relativeMedia)
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ) = BaseQuickBindingHolder(
        FragmentOverviewRelativeBinding.inflate(context.inflater, parent, false)
    )

    private class ItemAdapter : BaseQuickDiffBindingAdapter<MediaDetailEntity.MediaRelative,
            FragmentOverviewRelativeItemBinding>(DiffItemCallback) {
        override fun BaseQuickBindingHolder<FragmentOverviewRelativeItemBinding>.converted(item: MediaDetailEntity.MediaRelative) {
            binding.ivAvatar.loadImageAnimate(item.cover)
            binding.tvName.text = item.titleCn.ifBlank { item.titleNative }
            binding.tvSubtitle.text = item.titleNative
            binding.tvType.text = item.type
        }

        private object DiffItemCallback :
            DiffUtil.ItemCallback<MediaDetailEntity.MediaRelative>() {
            override fun areItemsTheSame(
                oldItem: MediaDetailEntity.MediaRelative,
                newItem: MediaDetailEntity.MediaRelative
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: MediaDetailEntity.MediaRelative,
                newItem: MediaDetailEntity.MediaRelative
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}