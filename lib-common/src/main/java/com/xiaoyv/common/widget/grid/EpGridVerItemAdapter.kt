package com.xiaoyv.common.widget.grid

import com.xiaoyv.common.api.response.api.ApiUserEpEntity
import com.xiaoyv.common.databinding.ViewEpVerItemBinding
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [EpGridVerItemAdapter]
 *
 * @author why
 * @since 12/22/23
 */
class EpGridVerItemAdapter :
    BaseQuickDiffBindingAdapter<ApiUserEpEntity, ViewEpVerItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<ViewEpVerItemBinding>.converted(item: ApiUserEpEntity) {
        EpGridHelper.converted(context, item, binding.tvEp, binding.tvEpType)
    }
}