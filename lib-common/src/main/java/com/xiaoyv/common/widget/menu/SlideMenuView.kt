package com.xiaoyv.common.widget.menu

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xiaoyv.common.R
import com.xiaoyv.common.databinding.ViewMenuSlideBinding
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.kts.tint
import com.xiaoyv.common.widget.scroll.AnimeLinearLayoutManager
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.getAttrColor

/**
 * Class: [SlideMenuView]
 *
 * @author why
 * @since 12/6/23
 */
class SlideMenuView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {
    private val itemAdapter = ItemAdapter()

    var menus: List<SlideMenu> = emptyList()
        set(value) {
            field = value
            itemAdapter.submitList(value)
        }

    var onItemSelectedListener: (SlideMenu) -> Unit = {}

    var selected: Int = -1
        set(value) {
            field = value
            itemAdapter.selected(value)
        }

    init {
        adapter = itemAdapter
        itemAnimator = null
        layoutManager = AnimeLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        itemAdapter.setOnDebouncedChildClickListener(R.id.tv_menu) {
            onItemSelectedListener(it)
        }
    }

    private class ItemAdapter :
        BaseQuickDiffBindingAdapter<SlideMenu, ViewMenuSlideBinding>(SlideMenuItemCallback) {
        private var selectedIndex = -1

        override fun onBindViewHolder(
            holder: BaseQuickBindingHolder<ViewMenuSlideBinding>,
            position: Int,
            item: SlideMenu?
        ) {
            super.onBindViewHolder(holder, position, item)
            if (position == selectedIndex) {
                holder.binding.tvMenu.backgroundTintList =
                    context.getAttrColor(GoogleAttr.colorPrimarySurface).tint
                holder.binding.tvMenu.setTextColor(
                    context.getAttrColor(GoogleAttr.colorOnPrimarySurface)
                )
            } else {
                holder.binding.tvMenu.backgroundTintList = Color.TRANSPARENT.tint
                holder.binding.tvMenu.setTextColor(
                    context.getAttrColor(GoogleAttr.colorOnSurfaceVariant)
                )
            }
        }

        override fun BaseQuickBindingHolder<ViewMenuSlideBinding>.converted(item: SlideMenu) {
            binding.tvMenu.text = item.title
        }

        fun selected(value: Int) {
            val old = selectedIndex
            selectedIndex = value
            if (old in 0..<itemCount) notifyItemChanged(old)
            if (value in 0..<itemCount) notifyItemChanged(selectedIndex)
        }
    }

    data class SlideMenu(var title: String, var value: String)

    private object SlideMenuItemCallback : DiffUtil.ItemCallback<SlideMenu>() {
        override fun areItemsTheSame(oldItem: SlideMenu, newItem: SlideMenu): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: SlideMenu, newItem: SlideMenu): Boolean {
            return oldItem == newItem
        }
    }
}