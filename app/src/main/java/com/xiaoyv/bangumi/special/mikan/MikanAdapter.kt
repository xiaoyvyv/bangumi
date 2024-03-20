package com.xiaoyv.bangumi.special.mikan

import com.xiaoyv.bangumi.databinding.ActivityMikanItemBinding
import com.xiaoyv.common.api.parser.entity.MikanEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.loadImage

/**
 * Class: [MikanAdapter]
 *
 * @author why
 * @since 3/20/24
 */
class MikanAdapter : BaseQuickDiffBindingAdapter<MikanEntity.Group,
        ActivityMikanItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<ActivityMikanItemBinding>.converted(item: MikanEntity.Group) {
        binding.ivAvatar.loadImage(item.poster)
        binding.tvTitle.text = item.name
        binding.tvDesc.text = item.time
    }
}