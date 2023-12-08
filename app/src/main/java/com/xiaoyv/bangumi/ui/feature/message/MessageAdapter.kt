package com.xiaoyv.bangumi.ui.feature.message

import com.xiaoyv.bangumi.databinding.ActivityNotifyItemBinding
import com.xiaoyv.common.api.parser.entity.MessageEntity
import com.xiaoyv.common.helper.IdDiffItemCallback
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [MessageAdapter]
 *
 * @author why
 * @since 12/8/23
 */
class MessageAdapter :
    BaseQuickDiffBindingAdapter<MessageEntity, ActivityNotifyItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<ActivityNotifyItemBinding>.converted(item: MessageEntity) {
        binding.ivAvatar.loadImageAnimate(item.fromAvatar)
        binding.tvContent.text = item.summary
        binding.tvTitle.text = item.fromName
        binding.tvTime.text = item.time
    }
}