package com.xiaoyv.common.widget.grid

import com.xiaoyv.common.api.parser.entity.MediaChapterEntity
import com.xiaoyv.common.databinding.ViewEpHorItemBinding
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [EpGridHorItemAdapter]
 *
 * @author why
 * @since 12/22/23
 */
class EpGridHorItemAdapter :
    BaseQuickDiffBindingAdapter<MediaChapterEntity, ViewEpHorItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<ViewEpHorItemBinding>.converted(item: MediaChapterEntity) {
        EpGridHelper.converted(context, item, binding.tvEp, binding.tvEpType)
    }
}