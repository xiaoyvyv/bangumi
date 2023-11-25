package com.xiaoyv.bangumi.ui.media

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.databinding.FragmentMediaOptionItemBinding
import com.xiaoyv.bangumi.databinding.FragmentMediaOptionTitleBinding
import com.xiaoyv.common.api.parser.entity.BrowserEntity
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.widget.binder.BaseQuickBindingHolder

/**
 * Class: [MediaOptionAdapter]
 *
 * @author why
 * @since 11/25/23
 */
class MediaOptionAdapter : BaseMultiItemAdapter<Any>() {

    init {
        this.addItemType(TYPE_TITLE, MediaOptionTitleBinder())
            .addItemType(TYPE_ITEM, MediaOptionItemBinder())
            .onItemViewType { position, list ->
                when (list[position]) {
                    is String -> TYPE_TITLE
                    is BrowserEntity.OptionItem -> TYPE_ITEM
                    else -> 1
                }
            }
    }

    companion object {
        const val TYPE_TITLE = 1
        const val TYPE_ITEM = 2
    }

    private class MediaOptionTitleBinder :
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

    private class MediaOptionItemBinder :
        OnMultiItemAdapterListener<Any, BaseQuickBindingHolder<FragmentMediaOptionItemBinding>> {
        override fun onBind(
            holder: BaseQuickBindingHolder<FragmentMediaOptionItemBinding>,
            position: Int,
            item: Any?
        ) {
            val content = item as? BrowserEntity.OptionItem
            val binding = holder.binding

            binding.tvOptionItem.text = content?.title
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