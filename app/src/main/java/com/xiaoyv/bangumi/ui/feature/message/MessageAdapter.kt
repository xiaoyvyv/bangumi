package com.xiaoyv.bangumi.ui.feature.message

import androidx.core.view.isVisible
import com.blankj.utilcode.util.SpanUtils
import com.xiaoyv.bangumi.databinding.ActivityNotifyItemBinding
import com.xiaoyv.common.api.parser.entity.MessageEntity
import com.xiaoyv.common.config.annotation.MessageBoxType
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.getAttrColor

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
        binding.tvTitle.text = item.fromName
        binding.tvTime.text = item.time
        binding.ivBadge.isVisible = item.boxType == MessageBoxType.TYPE_INBOX && item.isRead

        if (item.boxType == MessageBoxType.TYPE_OUTBOX && item.isRead) {
            SpanUtils.with(binding.tvContent)
                .append("[对方未读] ")
                .setForegroundColor(context.getAttrColor(GoogleAttr.colorPrimary))
                .append(item.summary)
                .create()
        } else {
            binding.tvContent.text = item.summary
        }
    }
}