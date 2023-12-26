package com.xiaoyv.bangumi.ui.media.detail.overview.binder

import android.content.Context
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentOverviewPreviewBinding
import com.xiaoyv.bangumi.databinding.FragmentOverviewTourItemBinding
import com.xiaoyv.common.api.response.anime.AnimeTourEntity
import com.xiaoyv.common.config.bean.AdapterTypeItem
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.helper.callback.RecyclerItemTouchedListener
import com.xiaoyv.common.kts.clear
import com.xiaoyv.common.kts.forceCast
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.formatHMS

/**
 * Class: [OverviewTourBinder]
 *
 * @author why
 * @since 11/30/23
 */
class OverviewTourBinder(
    private val touchedListener: RecyclerItemTouchedListener,
    private val clickItemListener: (AnimeTourEntity.LitePoint) -> Unit,
) : BaseMultiItemAdapter.OnMultiItemAdapterListener<AdapterTypeItem, BaseQuickBindingHolder<FragmentOverviewPreviewBinding>> {

    private val itemAdapter by lazy {
        ItemAdapter().apply {
            setOnDebouncedChildClickListener(R.id.item_tour, block = clickItemListener)
        }
    }

    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentOverviewPreviewBinding>,
        position: Int,
        item: AdapterTypeItem?,
    ) {
        item ?: return
        holder.binding.tvSectionPreview.title = item.title
        item.entity.forceCast<AnimeTourEntity>().apply {
            itemAdapter.submitList(litePoints.orEmpty())
        }
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
    ): BaseQuickBindingHolder<FragmentOverviewPreviewBinding> {
        val binding = FragmentOverviewPreviewBinding.inflate(context.inflater, parent, false)
        binding.rvPreview.adapter = itemAdapter
        binding.rvPreview.addOnItemTouchListener(touchedListener)
        return BaseQuickBindingHolder(binding)
    }

    private class ItemAdapter : BaseQuickDiffBindingAdapter<AnimeTourEntity.LitePoint,
            FragmentOverviewTourItemBinding>(IdDiffItemCallback()) {
        override fun BaseQuickBindingHolder<FragmentOverviewTourItemBinding>.converted(item: AnimeTourEntity.LitePoint) {
            // 没有图片不显示
            if (item.image.orEmpty().isBlank()) {
                binding.tvEmpty.isVisible = true
                binding.ivImage.clear()
                binding.ivImage.setImageResource(0)
            } else {
                binding.tvEmpty.isVisible = false
                binding.ivImage.loadImageAnimate(item.image.orEmpty().substringBefore("?"))
            }

            binding.tvEp.text = buildString {
                append("Ep ")
                append(item.ep)

                if (item.s != 0L) {
                    append("  ")
                    append(item.s.formatHMS())
                }
            }
            binding.tvPlace.text = item.cn.orEmpty().ifBlank { item.name }
        }
    }
}