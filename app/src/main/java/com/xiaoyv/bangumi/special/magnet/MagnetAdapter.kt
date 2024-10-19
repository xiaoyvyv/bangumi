package com.xiaoyv.bangumi.special.magnet

import android.graphics.Color
import com.blankj.utilcode.util.SpanUtils
import com.xiaoyv.bangumi.databinding.ActivityMagnetItemBinding
import com.xiaoyv.common.api.response.anime.AnimeMagnetEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.highlightText
import com.xiaoyv.common.kts.i18n
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [MagnetAdapter]
 *
 * @author why
 * @since 1/1/24
 */
class MagnetAdapter(private val keyword: () -> String) :
    BaseQuickDiffBindingAdapter<AnimeMagnetEntity.Resource,
            ActivityMagnetItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<ActivityMagnetItemBinding>.converted(item: AnimeMagnetEntity.Resource) {
        val time = item.publishDate.orEmpty().ifBlank { i18n(CommonString.type_unknown) }
        val size = item.fileSize.orEmpty().ifBlank { i18n(CommonString.type_unknown) }
        val subgroupName = item.subgroupName.orEmpty().ifBlank { i18n(CommonString.type_unknown) }
        val typeName = item.typeName.orEmpty().ifBlank { i18n(CommonString.type_unknown) }

        binding.tvTitle.text = item.title

        // 高亮
        binding.tvTitle.highlightText(keyword().split("\\s+".toRegex()), Color.GREEN)

        SpanUtils.with(binding.tvDesc)
            .append(i18n(CommonString.magnet_team, subgroupName))
            .append("\n")
            .append(i18n(CommonString.magnet_time, time))
            .append("\n")
            .append(i18n(CommonString.magnet_size, size))
            .append("\n")
            .append(i18n(CommonString.magnet_kind, typeName))
            .create()
    }
}