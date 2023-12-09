package com.xiaoyv.bangumi.ui.media.detail.overview.binder

import android.content.Context
import android.content.res.ColorStateList
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentOverviewEpBinding
import com.xiaoyv.bangumi.databinding.FragmentOverviewEpItemBinding
import com.xiaoyv.bangumi.ui.media.detail.overview.OverviewAdapter
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.helper.callback.RecyclerItemTouchedListener
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.forceCast
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.getAttrColor
import com.xiaoyv.widget.kts.subListLimit

/**
 * Class: [OverviewEpBinder]
 *
 * @author why
 * @since 11/30/23
 */
class OverviewEpBinder(
    private val touchedListener: RecyclerItemTouchedListener,
    private val clickItemListener: (MediaDetailEntity.MediaProgress) -> Unit
) : BaseMultiItemAdapter.OnMultiItemAdapterListener<OverviewAdapter.Item, BaseQuickBindingHolder<FragmentOverviewEpBinding>> {

    /**
     * 最大显示 48 个
     */
    private val subSize = 24

    private val itemAdapter by lazy {
        ItemAdapter().apply {
            setOnDebouncedChildClickListener(R.id.item_ep, block = clickItemListener)
        }
    }

    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentOverviewEpBinding>,
        position: Int,
        item: OverviewAdapter.Item?
    ) {
        item ?: return
        holder.binding.tvEpMyProgress.text = String.format("%s/10", 1)
        holder.binding.tvTitleEp.title = item.title
        itemAdapter.submitList(
            item.entity.forceCast<MediaDetailEntity>().progressList
                .subListLimit(subSize)
        )
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): BaseQuickBindingHolder<FragmentOverviewEpBinding> {
        val binding = FragmentOverviewEpBinding.inflate(context.inflater, parent, false)
        binding.rvEp.adapter = itemAdapter
        binding.rvEp.addOnItemTouchListener(touchedListener)
        return BaseQuickBindingHolder(binding)
    }

    private class ItemAdapter : BaseQuickDiffBindingAdapter<MediaDetailEntity.MediaProgress,
            FragmentOverviewEpItemBinding>(IdDiffItemCallback()) {
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
    }
}