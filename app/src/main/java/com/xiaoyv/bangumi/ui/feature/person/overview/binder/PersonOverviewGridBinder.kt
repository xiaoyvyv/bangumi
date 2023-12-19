package com.xiaoyv.bangumi.ui.feature.person.overview.binder

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentPersonOverviewGridBinding
import com.xiaoyv.bangumi.databinding.FragmentPersonOverviewGridItemBinding
import com.xiaoyv.common.config.bean.AdapterTypeItem
import com.xiaoyv.common.config.bean.SampleImageEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.helper.callback.RecyclerItemTouchedListener
import com.xiaoyv.common.kts.forceCast
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [PersonOverviewGridBinder]
 *
 * @author why
 * @since 12/6/23
 */
class PersonOverviewGridBinder(
    private val pool: RecyclerView.RecycledViewPool,
    private val touchedListener: RecyclerItemTouchedListener,
    private val clickItem: (SampleImageEntity) -> Unit
) : BaseMultiItemAdapter.OnMultiItemAdapterListener<AdapterTypeItem, BaseQuickBindingHolder<FragmentPersonOverviewGridBinding>> {

    private val itemAdapter by lazy {
        ItemAdapter().apply {
            setOnDebouncedChildClickListener(R.id.item_grid, block = clickItem)
        }
    }

    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentPersonOverviewGridBinding>,
        position: Int,
        item: AdapterTypeItem?
    ) {
        item ?: return
        holder.binding.tvItemTitle.text = item.title
        holder.binding.rvGrid.adapter = itemAdapter
        itemAdapter.submitList(item.entity.forceCast())
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): BaseQuickBindingHolder<FragmentPersonOverviewGridBinding> {
        val binding = FragmentPersonOverviewGridBinding.inflate(context.inflater, parent, false)
        binding.rvGrid.setRecycledViewPool(pool)
        binding.rvGrid.addOnItemTouchListener(touchedListener)
        return BaseQuickBindingHolder(binding)
    }

    /**
     * 横向 Grid
     */
    private class ItemAdapter : BaseQuickDiffBindingAdapter<SampleImageEntity,
            FragmentPersonOverviewGridItemBinding>(IdDiffItemCallback()) {
        override fun BaseQuickBindingHolder<FragmentPersonOverviewGridItemBinding>.converted(item: SampleImageEntity) {
            binding.ivAvatar.loadImageAnimate(item.image)
            binding.tvName.text = item.title
            binding.tvDesc.text = item.desc
        }
    }
}