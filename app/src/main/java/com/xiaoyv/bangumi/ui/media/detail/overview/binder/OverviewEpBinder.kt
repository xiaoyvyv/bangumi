package com.xiaoyv.bangumi.ui.media.detail.overview.binder

import android.content.Context
import android.content.res.ColorStateList
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentOverviewEpBinding
import com.xiaoyv.bangumi.databinding.FragmentOverviewEpItemBinding
import com.xiaoyv.bangumi.ui.media.detail.overview.OverviewAdapter
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.helper.RecyclerItemTouchedListener
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.getAttrColor

/**
 * Class: [OverviewEpBinder]
 *
 * @author why
 * @since 11/30/23
 */
class OverviewEpBinder(
    private val touchedListener: RecyclerItemTouchedListener,
    private val clickItemListener: (MediaDetailEntity.MediaProgress) -> Unit
) : BaseMultiItemAdapter.OnMultiItemAdapterListener<OverviewAdapter.OverviewItem, BaseQuickBindingHolder<FragmentOverviewEpBinding>> {

    private val itemAdapter by lazy {
        ItemAdapter()
    }

    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentOverviewEpBinding>,
        position: Int,
        item: OverviewAdapter.OverviewItem?
    ) {
        item ?: return
        holder.binding.tvEpMyProgress.text = String.format("%s/10", 1)
        holder.binding.rvEp.adapter = itemAdapter
        holder.binding.rvEp.addOnItemTouchListener(touchedListener)
        itemAdapter.submitList(item.mediaDetailEntity.progressList)
        itemAdapter.setOnDebouncedChildClickListener(R.id.item_ep, block = clickItemListener)
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ) = BaseQuickBindingHolder(
        FragmentOverviewEpBinding.inflate(context.inflater, parent, false)
    )

    private class ItemAdapter : BaseQuickDiffBindingAdapter<MediaDetailEntity.MediaProgress,
            FragmentOverviewEpItemBinding>(DiffItemCallback) {
        override fun BaseQuickBindingHolder<FragmentOverviewEpItemBinding>.converted(item: MediaDetailEntity.MediaProgress) {
            binding.tvEp.text = item.no

            when {
                item.isRelease -> {
                    binding.tvEp.setTextColor(context.getAttrColor(GoogleAttr.colorOnPrimaryContainer))
                    binding.tvEp.backgroundTintList = ColorStateList.valueOf(
                        context.getAttrColor(GoogleAttr.colorPrimaryContainer)
                    )
                }

                else -> {
                    binding.tvEp.setTextColor(context.getAttrColor(GoogleAttr.colorOnSurface))
                    binding.tvEp.backgroundTintList = ColorStateList.valueOf(
                        context.getAttrColor(GoogleAttr.colorSurfaceContainer)
                    )
                }
            }
        }

        private object DiffItemCallback :
            DiffUtil.ItemCallback<MediaDetailEntity.MediaProgress>() {
            override fun areItemsTheSame(
                oldItem: MediaDetailEntity.MediaProgress,
                newItem: MediaDetailEntity.MediaProgress
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: MediaDetailEntity.MediaProgress,
                newItem: MediaDetailEntity.MediaProgress
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}