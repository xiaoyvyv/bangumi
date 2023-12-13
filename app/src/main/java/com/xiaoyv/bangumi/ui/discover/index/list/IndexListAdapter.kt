package com.xiaoyv.bangumi.ui.discover.index.list

import com.xiaoyv.bangumi.databinding.FragmentIndexItemBinding
import com.xiaoyv.common.api.parser.entity.IndexItemEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [GroupListAdapter]
 *
 * @author why
 * @since 12/12/23
 */
class IndexListAdapter :
    BaseQuickDiffBindingAdapter<IndexItemEntity, FragmentIndexItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<FragmentIndexItemBinding>.converted(item: IndexItemEntity) {
        binding.ivAvatar.loadImageAnimate(item.userAvatar)
        binding.tvTitle.text = item.title
        binding.tvDesc.text = item.desc.ifBlank { "这个目录暂时没有描述呢" }
        binding.tvTime.text = item.time
    }
}