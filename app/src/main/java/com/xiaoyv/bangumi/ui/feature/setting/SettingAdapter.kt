package com.xiaoyv.bangumi.ui.feature.setting

import com.xiaoyv.bangumi.databinding.ActivityNotifyItemBinding
import com.xiaoyv.common.api.parser.entity.NotifyEntity
import com.xiaoyv.common.helper.IdDiffItemCallback
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [SettingAdapter]
 *
 * @author why
 * @since 12/8/23
 */
class SettingAdapter :
    BaseQuickDiffBindingAdapter<NotifyEntity, ActivityNotifyItemBinding>(IdDiffItemCallback()) {
    override fun BaseQuickBindingHolder<ActivityNotifyItemBinding>.converted(item: NotifyEntity) {
        binding.ivAvatar.loadImageAnimate(item.userAvatar)
        binding.tvContent.text = item.replyContent
        binding.tvTitle.text = item.userName
    }
}