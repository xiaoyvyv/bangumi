package com.xiaoyv.common.widget.dialog.filter

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.xiaoyv.common.R
import com.xiaoyv.common.config.bean.FilterEntity
import com.xiaoyv.common.databinding.ViewFilterBinding
import com.xiaoyv.common.databinding.ViewFilterItemBinding
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.kts.tint
import com.xiaoyv.common.widget.scroll.AnimeLinearLayoutManager
import com.xiaoyv.widget.binder.BaseQuickBindingAdapter
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.kts.getAttrColor

/**
 * Class: [FilterHeaderAdapter]
 *
 * @author why
 * @since 11/29/23
 */
class FilterHeaderAdapter(
    items: List<FilterEntity>,
    private val singleSelected: Boolean = true,
    private val flex: Boolean = false,
) : BaseQuickBindingAdapter<FilterEntity, ViewFilterBinding>(items) {
    var onSelectedChangeListener: (options: List<FilterEntity.OptionItem>) -> Unit = {}

    private val linearLayoutManager
        get() = AnimeLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

    private val flexboxLayoutManager
        get() = FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.WRAP)

    override fun BaseQuickBindingHolder<ViewFilterBinding>.converted(item: FilterEntity) {
        binding.tvOptionTitle.text = item.id

        // 设置选项数据
        (binding.rvItems.adapter as ItemAdapter).apply {
            submitList(item.options)
            notifyItemRangeChanged(0, itemCount)
        }
    }

    override fun isFullSpanItem(itemType: Int): Boolean {
        return true
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
    ): BaseQuickBindingHolder<ViewFilterBinding> {
        val viewHolder = super.onCreateViewHolder(context, parent, viewType)
        if (flex) {
            viewHolder.binding.rvItems.layoutManager = flexboxLayoutManager
        } else {
            viewHolder.binding.rvItems.layoutManager = linearLayoutManager
        }
        viewHolder.binding.rvItems.hasFixedSize()
        viewHolder.binding.rvItems.itemAnimator = null
        viewHolder.binding.rvItems.adapter = ItemAdapter().apply {
            setOnDebouncedChildClickListener(R.id.tv_option_item) {
                onSelectedChange(this, it)
            }
        }
        return viewHolder
    }

    private fun onSelectedChange(subAdapter: ItemAdapter, optionItem: FilterEntity.OptionItem) {
        // 全局单选
        if (singleSelected) {
            items.flatMap { it.options }.forEach { item ->
                if (item == optionItem) {
                    item.selected = !item.selected
                } else {
                    item.selected = false
                }
            }
            notifyItemRangeChanged(0, itemCount)
        }
        // 过滤项目单独单选
        else {
            subAdapter.items.forEach { item ->
                if (item == optionItem) {
                    item.selected = !item.selected
                } else {
                    item.selected = false
                }
            }
            subAdapter.notifyItemRangeChanged(0, subAdapter.itemCount)
        }

        val selectedOptions = items.flatMap { it.options }.filter { it.selected }
        onSelectedChangeListener(selectedOptions)
    }

    private class ItemAdapter :
        BaseQuickBindingAdapter<FilterEntity.OptionItem, ViewFilterItemBinding>() {

        override fun BaseQuickBindingHolder<ViewFilterItemBinding>.converted(item: FilterEntity.OptionItem) {
            binding.tvOptionItem.text = item.title
            if (item.selected) {
                binding.tvOptionItem.setTextColor(context.getAttrColor(GoogleAttr.colorPrimary))
                binding.tvOptionItem.backgroundTintList =
                    context.getAttrColor(GoogleAttr.colorSurfaceContainer).tint
            } else {
                binding.tvOptionItem.setTextColor(context.getAttrColor(GoogleAttr.colorOnSurface))
                binding.tvOptionItem.backgroundTintList = Color.TRANSPARENT.tint
            }
        }
    }
}