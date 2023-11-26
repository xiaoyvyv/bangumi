package com.xiaoyv.bangumi.ui.media.option

import android.content.Context
import android.content.res.ColorStateList
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.databinding.FragmentMediaOptionItemBinding
import com.xiaoyv.bangumi.databinding.FragmentMediaOptionTitleBinding
import com.xiaoyv.common.config.bean.MediaOptionConfig
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.kts.getAttrColor

/**
 * Class: [MediaOptionAdapter]
 *
 * @author why
 * @since 11/25/23
 */
class MediaOptionAdapter : BaseMultiItemAdapter<Any>() {
    val selected = arrayListOf<MediaOptionConfig.Config.Option.Item>()

    init {
        this.addItemType(TYPE_TITLE, MediaOptionTitleBinder())
            .addItemType(TYPE_ITEM, MediaOptionItemBinder())
            .onItemViewType { position, list ->
                when (list[position]) {
                    is String -> TYPE_TITLE
                    is MediaOptionConfig.Config.Option.Item -> TYPE_ITEM
                    else -> 1
                }
            }
    }

    fun selectItem(any: MediaOptionConfig.Config.Option.Item) {
        if (selected.contains(any)) return

        // 移除同类型的已选的选项
        for (i in selected.size - 1 downTo 0) {
            val item = selected[i]
            if (item.groupTitle == any.groupTitle) {
                selected.remove(item)
                notifyItemChanged(itemIndexOfFirst(item))
            }
        }

        selected.add(any)
        notifyItemChanged(itemIndexOfFirst(any))
    }

    fun unselectItem(any: MediaOptionConfig.Config.Option.Item) {
        if (!selected.contains(any)) return
        selected.remove(any)
        notifyItemChanged(itemIndexOfFirst(any))
    }

    fun isSelected(it: MediaOptionConfig.Config.Option.Item): Boolean {
        return selected.indexOf(it) != -1
    }

    companion object {
        const val TYPE_TITLE = 1
        const val TYPE_ITEM = 2
    }

    private inner class MediaOptionTitleBinder :
        OnMultiItemAdapterListener<Any, BaseQuickBindingHolder<FragmentMediaOptionTitleBinding>> {
        override fun onBind(
            holder: BaseQuickBindingHolder<FragmentMediaOptionTitleBinding>,
            position: Int,
            item: Any?
        ) {
            holder.binding.tvOptionTitle.text = item.toString()
        }

        override fun onCreate(
            context: Context,
            parent: ViewGroup,
            viewType: Int
        ): BaseQuickBindingHolder<FragmentMediaOptionTitleBinding> {
            return BaseQuickBindingHolder(
                FragmentMediaOptionTitleBinding.inflate(
                    context.inflater,
                    parent,
                    false
                )
            )
        }

        override fun isFullSpanItem(itemType: Int): Boolean {
            return true
        }
    }

    private inner class MediaOptionItemBinder :
        OnMultiItemAdapterListener<Any, BaseQuickBindingHolder<FragmentMediaOptionItemBinding>> {
        override fun onBind(
            holder: BaseQuickBindingHolder<FragmentMediaOptionItemBinding>,
            position: Int,
            item: Any?
        ) {
            val content = item as? MediaOptionConfig.Config.Option.Item
            val binding = holder.binding

            binding.tvOptionItem.text = content?.title
            binding.tvOptionItem.backgroundTintList = ColorStateList.valueOf(
                if (selected.contains(item)) {
                    context.getAttrColor(GoogleAttr.colorSurfaceVariant)
                } else {
                    context.getAttrColor(GoogleAttr.colorSurfaceContainer)
                }
            )
        }

        override fun onCreate(
            context: Context,
            parent: ViewGroup,
            viewType: Int
        ): BaseQuickBindingHolder<FragmentMediaOptionItemBinding> {
            return BaseQuickBindingHolder(
                FragmentMediaOptionItemBinding.inflate(
                    context.inflater,
                    parent,
                    false
                )
            )
        }
    }
}