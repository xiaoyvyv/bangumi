package com.xiaoyv.bangumi.ui.feature.notify

import androidx.core.view.isVisible
import com.xiaoyv.bangumi.databinding.ActivityNotifyItemBinding
import com.xiaoyv.common.api.parser.entity.NotifyEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [NotifyAdapter]
 *
 * @author why
 * @since 12/8/23
 */
class NotifyAdapter(var notifyCount: Int) :
    BaseQuickDiffBindingAdapter<NotifyEntity, ActivityNotifyItemBinding>(IdDiffItemCallback()) {

    override fun onBindViewHolder(
        holder: BaseQuickBindingHolder<ActivityNotifyItemBinding>,
        position: Int,
        item: NotifyEntity?,
    ) {
        super.onBindViewHolder(holder, position, item)
        holder.binding.ivBadge.isVisible = position + 1 <= notifyCount
        holder.binding.btnAction.isVisible =
            (position + 1 <= notifyCount) && item?.isAddFriend == true
        holder.binding.btnIgnore.isVisible =
            (position + 1 <= notifyCount) && item?.isAddFriend == true
    }

    override fun BaseQuickBindingHolder<ActivityNotifyItemBinding>.converted(item: NotifyEntity) {
        binding.ivAvatar.loadImageAnimate(item.userAvatar)
        binding.tvContent.text = item.replyContent
        binding.tvTitle.text = item.userName
    }
}