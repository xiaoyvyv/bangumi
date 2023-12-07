package com.xiaoyv.bangumi.ui.discover.group.detail

import com.xiaoyv.bangumi.databinding.ActivityGroupDetailItemBinding
import com.xiaoyv.common.config.bean.SampleAvatar
import com.xiaoyv.common.helper.IdDiffItemCallback
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [GroupDetailAdapter]
 */
class GroupDetailAdapter : BaseQuickDiffBindingAdapter<SampleAvatar,
        ActivityGroupDetailItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<ActivityGroupDetailItemBinding>.converted(item: SampleAvatar) {
        binding.ivAvatar.loadImageAnimate(item.image)
        binding.tvTip.text = item.title
    }
}