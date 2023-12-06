package com.xiaoyv.bangumi.ui.feature.person.overview.binder

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentPersonOverviewGridBinding
import com.xiaoyv.bangumi.databinding.FragmentPersonOverviewGridItemBinding
import com.xiaoyv.bangumi.ui.feature.person.overview.PersonOverviewAdapter
import com.xiaoyv.common.config.bean.SampleAvatar
import com.xiaoyv.common.helper.RecyclerItemTouchedListener
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
    private val clickItem: (SampleAvatar) -> Unit
) : BaseMultiItemAdapter.OnMultiItemAdapterListener<PersonOverviewAdapter.Item, BaseQuickBindingHolder<FragmentPersonOverviewGridBinding>> {

    private val itemAdapter by lazy {
        ItemAdapter().apply {
            setOnDebouncedChildClickListener(R.id.item_grid, block = clickItem)
        }
    }

    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentPersonOverviewGridBinding>,
        position: Int,
        item: PersonOverviewAdapter.Item?
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
    private class ItemAdapter : BaseQuickDiffBindingAdapter<SampleAvatar,
            FragmentPersonOverviewGridItemBinding>(DiffItemCallback) {
        override fun BaseQuickBindingHolder<FragmentPersonOverviewGridItemBinding>.converted(item: SampleAvatar) {
            binding.ivAvatar.loadImageAnimate(item.image)
            binding.tvName.text = item.title
            binding.tvDesc.text = item.desc
        }

        private object DiffItemCallback :
            DiffUtil.ItemCallback<SampleAvatar>() {
            override fun areItemsTheSame(
                oldItem: SampleAvatar,
                newItem: SampleAvatar
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: SampleAvatar,
                newItem: SampleAvatar
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}