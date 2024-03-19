package com.xiaoyv.bangumi.special.yuc

import com.xiaoyv.bangumi.databinding.ActivityYucItemBinding
import com.xiaoyv.common.config.bean.YucSectionEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [YucAdapter]
 *
 * @author why
 * @since 3/19/24
 */
class YucAdapter : BaseQuickDiffBindingAdapter<YucSectionEntity,
        ActivityYucItemBinding>(IdDiffItemCallback()) {


    override fun BaseQuickBindingHolder<ActivityYucItemBinding>.converted(item: YucSectionEntity) {
        binding.sectionView.title = "${item.id}年"
        binding.sectionView.more = null
    }
}