package com.xiaoyv.bangumi.ui.timeline.page.binder

import android.content.Context
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.databinding.FragmentTimelinePageTextBinding
import com.xiaoyv.common.api.parser.entity.TimelineEntity
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder

/**
 * Class: [TimelineTextBinder]
 *
 * @author why
 * @since 12/12/23
 */
class TimelineTextBinder : BaseMultiItemAdapter.OnMultiItemAdapterListener<TimelineEntity,
        BaseQuickBindingHolder<FragmentTimelinePageTextBinding>> {

    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentTimelinePageTextBinding>,
        position: Int,
        item: TimelineEntity?
    ) {
        val entity = item ?: return

        holder.binding.ivAvatar.isInvisible = entity.avatar.isBlank()
        holder.binding.ivAvatar.loadImageAnimate(entity.avatar)
        holder.binding.tvAction.text = entity.title
        holder.binding.tvContent.text = entity.content
        holder.binding.tvContent.isVisible = entity.content.isNotBlank()
        holder.binding.tvTime.text = entity.time
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): BaseQuickBindingHolder<FragmentTimelinePageTextBinding> {
        return BaseQuickBindingHolder(
            FragmentTimelinePageTextBinding.inflate(context.inflater, parent, false)
        )
    }
}