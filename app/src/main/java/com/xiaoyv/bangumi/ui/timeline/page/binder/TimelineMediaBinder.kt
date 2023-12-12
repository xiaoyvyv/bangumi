package com.xiaoyv.bangumi.ui.timeline.page.binder

import android.content.Context
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.databinding.FragmentTimelinePageMediaBinding
import com.xiaoyv.common.api.parser.entity.TimelineEntity
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder

/**
 * Class: [TimelineMediaBinder]
 *
 * @author why
 * @since 12/12/23
 */
class TimelineMediaBinder :
    BaseMultiItemAdapter.OnMultiItemAdapterListener<TimelineEntity,
            BaseQuickBindingHolder<FragmentTimelinePageMediaBinding>> {

    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentTimelinePageMediaBinding>,
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

        holder.binding.ivMediaScore.rating = entity.mediaCard.score / 2f
        holder.binding.ivMediaScore.isVisible = entity.mediaCard.score != 0f
        holder.binding.tvMediaName.text = entity.mediaCard.title
        holder.binding.tvMediaDesc.text = entity.mediaCard.info
        holder.binding.ivMediaCover.loadImageAnimate(entity.mediaCard.cover)
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): BaseQuickBindingHolder<FragmentTimelinePageMediaBinding> {
        return BaseQuickBindingHolder(
            FragmentTimelinePageMediaBinding.inflate(context.inflater, parent, false)
        )
    }
}