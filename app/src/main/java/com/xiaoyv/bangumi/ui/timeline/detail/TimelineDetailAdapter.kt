package com.xiaoyv.bangumi.ui.timeline.detail

import androidx.core.text.method.LinkMovementMethodCompat
import com.xiaoyv.bangumi.databinding.ActivityTimelineDetailHeaderBinding
import com.xiaoyv.bangumi.databinding.ActivityTimelineDetailItemBinding
import com.xiaoyv.bangumi.ui.timeline.page.TimelinePageAdapter
import com.xiaoyv.common.api.parser.entity.TimelineDetailEntity
import com.xiaoyv.common.api.parser.entity.TimelineReplyEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

class TimelineDetailHeader : BaseQuickDiffBindingAdapter<TimelineDetailEntity,
        ActivityTimelineDetailHeaderBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<ActivityTimelineDetailHeaderBinding>.converted(item: TimelineDetailEntity) {
        TimelinePageAdapter.onBindText(binding.layoutTimeline, item.detail, false)

        binding.sectionComment.title = "回复"
        binding.sectionComment.more = null
    }
}

class TimelineDetailAdapter : BaseQuickDiffBindingAdapter<TimelineReplyEntity,
        ActivityTimelineDetailItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<ActivityTimelineDetailItemBinding>.converted(item: TimelineReplyEntity) {
        binding.tvContent.text = item.content
        binding.tvContent.movementMethod = LinkMovementMethodCompat.getInstance()
    }
}