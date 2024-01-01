package com.xiaoyv.bangumi.special.magnet

import android.graphics.Color
import com.blankj.utilcode.util.SpanUtils
import com.xiaoyv.bangumi.databinding.ActivityMagnetItemBinding
import com.xiaoyv.common.api.response.anime.AnimeMagnetEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.highlightText
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
        val time = item.publishDate.orEmpty().ifBlank { "未知" }
        val size = item.fileSize.orEmpty().ifBlank { "未知" }
        val subgroupName = item.subgroupName.orEmpty().ifBlank { "未知" }
        val typeName = item.typeName.orEmpty().ifBlank { "未知" }

        binding.tvTitle.text = item.title

        // 高亮
        binding.tvTitle.highlightText(keyword().split("\\s+".toRegex()), Color.GREEN)

        SpanUtils.with(binding.tvDesc)
            .append("Team: $subgroupName\n")
            .append("Time: $time\n")
            .append("Size: $size\n")
            .append("Kind: $typeName")
            .create()
    }
}