package com.xiaoyv.bangumi.ui.feature.setting.translate.model

import android.graphics.Color
import com.xiaoyv.bangumi.databinding.ActivityTranslateModelItemBinding
import com.xiaoyv.common.config.bean.LanguageModel
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.getAttrColor

/**
 * Class: [TranslateModelAdapter]
 *
 * @author why
 * @since 1/24/24
 */
class TranslateModelAdapter : BaseQuickDiffBindingAdapter<LanguageModel,
        ActivityTranslateModelItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<ActivityTranslateModelItemBinding>.converted(item: LanguageModel) {
        binding.tvTitle.text = item.name
        binding.tvStatus.text = if (item.download) "已下载" else "未下载"
        binding.tvStatus.setTextColor(
            if (item.download) Color.GREEN else
                context.getAttrColor(GoogleAttr.colorOnSurfaceVariant)
        )
    }
}