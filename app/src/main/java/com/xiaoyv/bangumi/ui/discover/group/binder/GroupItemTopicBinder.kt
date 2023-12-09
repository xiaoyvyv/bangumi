package com.xiaoyv.bangumi.ui.discover.group.binder

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentGroupTopicBinding
import com.xiaoyv.bangumi.databinding.FragmentGroupTopicItemBinding
import com.xiaoyv.bangumi.ui.discover.group.GroupAdapter
import com.xiaoyv.common.api.parser.entity.TopicSampleEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.forceCast
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [GroupItemTopicBinder]
 *
 * @author why
 * @since 12/8/23
 */
class GroupItemTopicBinder(
    private val onClickTopicListener: (TopicSampleEntity) -> Unit,
) : BaseMultiItemAdapter.OnMultiItemAdapterListener<GroupAdapter.Item, BaseQuickBindingHolder<FragmentGroupTopicBinding>> {

    private val topicAdapter by lazy {
        TopicItemAdapter().apply {
            setOnDebouncedChildClickListener(R.id.item_topic, block = onClickTopicListener)
        }
    }

    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentGroupTopicBinding>,
        position: Int,
        item: GroupAdapter.Item?
    ) {
        item ?: return
        holder.binding.sectionTopic.title = item.title
        holder.binding.sectionTopic.more = null
        topicAdapter.submitList(item.entity.forceCast())
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): BaseQuickBindingHolder<FragmentGroupTopicBinding> {
        val binding = FragmentGroupTopicBinding.inflate(context.inflater, parent, false)
        binding.rvTopic.adapter = topicAdapter
        return BaseQuickBindingHolder(binding)
    }

    private class TopicItemAdapter : BaseQuickDiffBindingAdapter<TopicSampleEntity,
            FragmentGroupTopicItemBinding>(IdDiffItemCallback()) {

        override fun BaseQuickBindingHolder<FragmentGroupTopicItemBinding>.converted(item: TopicSampleEntity) {
            binding.tvTitle.text = String.format("%s：%s", item.userName, item.title)
            binding.tvTime.text = item.time
            binding.tvReplay.text = String.format("讨论：%d", item.commentCount)
            binding.tvContent.text = item.groupName
        }
    }
}