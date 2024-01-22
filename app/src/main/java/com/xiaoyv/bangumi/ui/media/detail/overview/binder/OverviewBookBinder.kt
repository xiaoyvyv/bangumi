package com.xiaoyv.bangumi.ui.media.detail.overview.binder

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentOverviewBookBinding
import com.xiaoyv.bangumi.databinding.FragmentOverviewBookItemBinding
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.config.bean.AdapterTypeItem
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.helper.callback.RecyclerItemTouchedListener
import com.xiaoyv.common.kts.forceCast
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [OverviewBookBinder]
 *
 * @author why
 * @since 12/6/23
 */
class OverviewBookBinder(
    private val touchedListener: RecyclerItemTouchedListener,
    private val clickItem: (MediaDetailEntity.MediaRelative) -> Unit,
) : BaseMultiItemAdapter.OnMultiItemAdapterListener<AdapterTypeItem, BaseQuickBindingHolder<FragmentOverviewBookBinding>> {

    private val itemAdapter by lazy {
        ItemAdapter().apply {
            setOnDebouncedChildClickListener(R.id.item_grid, block = clickItem)
        }
    }

    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentOverviewBookBinding>,
        position: Int,
        item: AdapterTypeItem?,
    ) {
        item ?: return
        holder.binding.sectionBook.title = item.title
        holder.binding.sectionBook.more = null
        holder.binding.rvGrid.adapter = itemAdapter
        itemAdapter.submitList(item.entity.forceCast())
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
    ): BaseQuickBindingHolder<FragmentOverviewBookBinding> {
        val binding = FragmentOverviewBookBinding.inflate(context.inflater, parent, false)
        binding.rvGrid.addOnItemTouchListener(touchedListener)
        return BaseQuickBindingHolder(binding)
    }

    /**
     * 横向 Grid
     */
    private class ItemAdapter : BaseQuickDiffBindingAdapter<MediaDetailEntity.MediaRelative,
            FragmentOverviewBookItemBinding>(IdDiffItemCallback()) {
        override fun BaseQuickBindingHolder<FragmentOverviewBookItemBinding>.converted(item: MediaDetailEntity.MediaRelative) {
            binding.ivAvatar.loadImageAnimate(item.cover)
            binding.tvNo.text = item.titleCn
        }
    }
}