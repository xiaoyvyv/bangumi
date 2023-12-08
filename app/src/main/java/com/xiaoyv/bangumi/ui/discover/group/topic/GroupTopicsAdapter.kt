package com.xiaoyv.bangumi.ui.discover.group.topic

import com.xiaoyv.bangumi.databinding.FragmentGroupTopicItemBinding
import com.xiaoyv.common.api.parser.entity.TopicSampleEntity
import com.xiaoyv.common.helper.IdDiffItemCallback
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [GroupTopicsAdapter]
 *
 * @author why
 * @since 11/24/23
 */
class GroupTopicsAdapter : BaseQuickDiffBindingAdapter<TopicSampleEntity,
        FragmentGroupTopicItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<FragmentGroupTopicItemBinding>.converted(item: TopicSampleEntity) {
        binding.tvTitle.text = item.title
        binding.tvTime.text = item.time
        binding.tvReplay.text = String.format("讨论：%d", item.commentCount)
        binding.tvContent.text = String.format("%s / %s", item.groupName, item.userName)
    }
}