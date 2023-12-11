package com.xiaoyv.bangumi.ui.feature.magi.history

import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.SpanUtils
import com.xiaoyv.bangumi.databinding.FragmentMagiHistoryItemBinding
import com.xiaoyv.common.api.parser.entity.MagiQuestionEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.CommonColor
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.getAttrColor

/**
 * Class: [MagiHistoryAdapter]
 *
 * @author why
 * @since 12/11/23
 */
class MagiHistoryAdapter : BaseQuickDiffBindingAdapter<MagiQuestionEntity,
        FragmentMagiHistoryItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<FragmentMagiHistoryItemBinding>.converted(item: MagiQuestionEntity) {
        binding.tvTitle.text = item.lastQuestionTitle
        if (item.lastQuestionRight) {
            binding.vState.setBackgroundColor(ColorUtils.getColor(CommonColor.question_right))
        } else {
            binding.vState.setBackgroundColor(ColorUtils.getColor(CommonColor.question_error))
        }
        SpanUtils.with(binding.tvContent)
            .append("通过率: ")
            .append(item.lastQuestionRightRate)
            .setForegroundColor(context.getAttrColor(GoogleAttr.colorPrimary))
            .append(item.lastQuestionCount)
            .create()
    }
}