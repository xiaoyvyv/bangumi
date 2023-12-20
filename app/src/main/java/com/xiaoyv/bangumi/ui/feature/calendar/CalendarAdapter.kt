package com.xiaoyv.bangumi.ui.feature.calendar

import android.content.Context
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.databinding.ActivityCalendarItemBinding
import com.xiaoyv.bangumi.databinding.ActivityCalendarTitleBinding
import com.xiaoyv.common.api.response.CalendarEntity
import com.xiaoyv.common.kts.IMAGE_HOLDER_3X4
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.kts.useNotNull

/**
 * Class: [CalendarAdapter]
 *
 * @author why
 * @since 11/25/23
 */
class CalendarAdapter : BaseMultiItemAdapter<Any>() {

    init {
        this.addItemType(TYPE_HEADER, CalendarItemTitleBinder())
            .addItemType(TYPE_GARD, CalendarItemContentBinder())
            .onItemViewType { position, list ->
                when (list[position]) {
                    is CalendarEntity.CalendarEntityItem.Weekday -> TYPE_HEADER
                    is CalendarEntity.CalendarEntityItem.Item -> TYPE_GARD
                    else -> 1
                }
            }
    }

    companion object {
        const val TYPE_HEADER = 1
        const val TYPE_GARD = 2
    }

    class CalendarItemTitleBinder :
        OnMultiItemAdapterListener<Any, BaseQuickBindingHolder<ActivityCalendarTitleBinding>> {
        override fun onBind(
            holder: BaseQuickBindingHolder<ActivityCalendarTitleBinding>,
            position: Int,
            item: Any?,
        ) {
            useNotNull(item as? CalendarEntity.CalendarEntityItem.Weekday) {
                holder.binding.tvCalendarTitle.text = cn ?: en ?: ja
            }
        }

        override fun onCreate(
            context: Context,
            parent: ViewGroup,
            viewType: Int,
        ): BaseQuickBindingHolder<ActivityCalendarTitleBinding> {
            return BaseQuickBindingHolder(
                ActivityCalendarTitleBinding.inflate(
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

    class CalendarItemContentBinder :
        OnMultiItemAdapterListener<Any, BaseQuickBindingHolder<ActivityCalendarItemBinding>> {
        override fun onBind(
            holder: BaseQuickBindingHolder<ActivityCalendarItemBinding>,
            position: Int,
            item: Any?,
        ) {
            val content = item as? CalendarEntity.CalendarEntityItem.Item
            val binding = holder.binding
            val score = content?.rating?.score ?: 0.0
            val epsCount = content?.epsCount ?: 0

            binding.ivCover.loadImageAnimate(content?.images?.large, holderType = IMAGE_HOLDER_3X4)
            binding.tvTitle.text = content?.nameCn.orEmpty().ifBlank { content?.name }

            binding.tvSummary.text = content?.summary.orEmpty()
            binding.tvSummary.isVisible = content?.summary.orEmpty().isNotBlank()

            binding.tvRank.text = if (score == 0.0) "暂无" else score.toString()

            binding.tvEps.text = epsCount.toString()
            binding.tvEps.isVisible = epsCount != 0
        }

        override fun onCreate(
            context: Context,
            parent: ViewGroup,
            viewType: Int,
        ): BaseQuickBindingHolder<ActivityCalendarItemBinding> {
            return BaseQuickBindingHolder(
                ActivityCalendarItemBinding.inflate(
                    context.inflater,
                    parent,
                    false
                )
            )
        }
    }
}