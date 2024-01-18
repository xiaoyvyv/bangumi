package com.xiaoyv.bangumi.ui.feature.search.detail.adapter

import android.graphics.Color
import com.blankj.utilcode.util.TimeUtils
import com.xiaoyv.bangumi.databinding.FragmentIndexItemBinding
import com.xiaoyv.common.api.parser.entity.SearchResultEntity
import com.xiaoyv.common.api.response.SearchApiIndexEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.highlightText
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [SearchIndexAdapter]
 *
 * @author why
 * @since 12/12/23
 */
class SearchIndexAdapter(val keyword: () -> List<String>) :
    BaseQuickDiffBindingAdapter<SearchResultEntity, FragmentIndexItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<FragmentIndexItemBinding>.converted(item: SearchResultEntity) {
        val entity = item.payload as SearchApiIndexEntity

        binding.tvAvatar.text = entity.nickname.orEmpty()
            .ifBlank { entity.username }.orEmpty()
            .firstOrNull()?.toString().orEmpty()

        binding.tvTitle.text = entity.title
        binding.tvDesc.text = entity.description.orEmpty().ifBlank { "这个目录暂时没有描述呢" }
        binding.tvTime.text = TimeUtils.millis2String(entity.updatedAt, "yyyy-MM-dd")

        // 高亮
        binding.tvTitle.highlightText(keyword(), Color.GREEN)
        binding.tvDesc.highlightText(keyword(), Color.GREEN)
    }
}