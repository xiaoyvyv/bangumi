package com.xiaoyv.bangumi.ui.feature.almanac

import com.xiaoyv.bangumi.databinding.ActivityAlmanacItemBinding
import com.xiaoyv.common.config.bean.AlmanacEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [AlmanacAdapter]
 *
 * @author why
 * @since 12/10/23
 */
class AlmanacAdapter : BaseQuickDiffBindingAdapter<AlmanacEntity,
        ActivityAlmanacItemBinding>(IdDiffItemCallback()) {
    override fun BaseQuickBindingHolder<ActivityAlmanacItemBinding>.converted(item: AlmanacEntity) {
        binding.tvYear.text = item.id
    }
}