package com.xiaoyv.bangumi.ui.feature.user.overview

import android.content.Context
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.databinding.FragmentUserOverviewItemBinding
import com.xiaoyv.bangumi.databinding.FragmentUserOverviewTitleBinding
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.api.parser.entity.UserDetailEntity
import com.xiaoyv.common.config.annotation.InterestType
import com.xiaoyv.common.kts.clear
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.kts.dpi

/**
 * Class: [UserOverviewAdapter]
 *
 * @author why
 * @since 12/4/23
 */
class UserOverviewAdapter : BaseMultiItemAdapter<Any>() {
    init {
        this.addItemType(TYPE_TITLE, ItemTitleBinder())
            .addItemType(TYPE_GRID, ItemGridBinder())
            .onItemViewType { position, list ->
                when (list[position]) {
                    is UserDetailEntity.SaveOverview -> TYPE_TITLE
                    is MediaDetailEntity.MediaRelative -> TYPE_GRID
                    else -> throw IllegalArgumentException()
                }
            }
    }

    /**
     * 收藏的媒体标题
     */
    private class ItemTitleBinder :
        OnMultiItemAdapterListener<Any, BaseQuickBindingHolder<FragmentUserOverviewTitleBinding>> {
        override fun onBind(
            holder: BaseQuickBindingHolder<FragmentUserOverviewTitleBinding>,
            position: Int,
            item: Any?,
        ) {
            val overview = item as? UserDetailEntity.SaveOverview ?: return
            holder.binding.tvSection.title = overview.title
            holder.binding.tvSection.more = null
            holder.binding.tvDesc.text = overview.count.joinToString("、")
        }

        override fun onCreate(
            context: Context,
            parent: ViewGroup,
            viewType: Int,
        ): BaseQuickBindingHolder<FragmentUserOverviewTitleBinding> {
            val binding = FragmentUserOverviewTitleBinding.inflate(context.inflater, parent, false)
            binding.tvSection.leftPadding = 8.dpi
            return BaseQuickBindingHolder(binding)
        }

        override fun isFullSpanItem(itemType: Int): Boolean {
            return true
        }
    }

    /**
     * 用户页面收藏的媒体表格
     */
    private class ItemGridBinder :
        OnMultiItemAdapterListener<Any, BaseQuickBindingHolder<FragmentUserOverviewItemBinding>> {
        override fun onBind(
            holder: BaseQuickBindingHolder<FragmentUserOverviewItemBinding>,
            position: Int,
            item: Any?,
        ) {
            val relative = item as? MediaDetailEntity.MediaRelative ?: return
            if (relative.id.isBlank()) {
                holder.binding.tvTip.isInvisible = true
                holder.binding.ivCover.isInvisible = true
                holder.binding.ivCover.clear()
            } else {
                holder.binding.tvTip.isVisible = true
                holder.binding.ivCover.isVisible = true
                holder.binding.ivCover.loadImageAnimate(relative.cover)
            }

            holder.binding.tvTip.text = InterestType.string(relative.collectType, relative.type)
        }

        override fun onCreate(
            context: Context,
            parent: ViewGroup,
            viewType: Int,
        ): BaseQuickBindingHolder<FragmentUserOverviewItemBinding> {
            return BaseQuickBindingHolder(
                FragmentUserOverviewItemBinding.inflate(context.inflater, parent, false)
            )
        }
    }

    companion object {
        const val TYPE_TITLE = 0
        const val TYPE_GRID = 1
    }
}