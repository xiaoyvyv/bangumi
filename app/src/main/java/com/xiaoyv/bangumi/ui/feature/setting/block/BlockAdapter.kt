package com.xiaoyv.bangumi.ui.feature.setting.block

import com.xiaoyv.bangumi.databinding.ActivityBlockItemBinding
import com.xiaoyv.common.api.parser.entity.BlockEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [BlockAdapter]
 *
 * @author why
 * @since 12/17/23
 */
class BlockAdapter :
    BaseQuickDiffBindingAdapter<BlockEntity, ActivityBlockItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<ActivityBlockItemBinding>.converted(item: BlockEntity) {
        binding.tvTitle.text = item.name
    }
}