package com.xiaoyv.bangumi.ui.feature.message.detail

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.xiaoyv.bangumi.databinding.ActivityMessageDetailItemBinding
import com.xiaoyv.common.api.parser.entity.MessageEntity
import com.xiaoyv.common.helper.IdDiffItemCallback
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [MessageDetailAdapter]
 *
 * @author why
 * @since 12/8/23
 */
class MessageDetailAdapter :
    BaseQuickDiffBindingAdapter<MessageEntity, ActivityMessageDetailItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<ActivityMessageDetailItemBinding>.converted(item: MessageEntity) {
        binding.ivAvatarTheme.loadImageAnimate(item.fromAvatar)
        binding.ivAvatarTheme.isVisible = !item.isMine

        binding.ivAvatarMine.loadImageAnimate(item.mineAvatar)
        binding.ivAvatarMine.isVisible = item.isMine

        binding.tvContent.text = item.summary
        binding.tvTime.text = item.time
        binding.pbProgress.isVisible = item.isSending

        binding.tvContent.updateLayoutParams<ConstraintLayout.LayoutParams> {
            horizontalBias = if (item.isMine) 1f else 0f
        }
        binding.tvTime.updateLayoutParams<ConstraintLayout.LayoutParams> {
            horizontalBias = if (item.isMine) 1f else 0f
        }

        binding.pbProgress.updateLayoutParams<ConstraintLayout.LayoutParams> {
            if (item.isMine) {
                startToStart = binding.tvContent.id
                endToEnd = ConstraintLayout.NO_ID
            } else {
                startToStart = ConstraintLayout.NO_ID
                endToEnd = binding.tvContent.id
            }
        }
    }
}