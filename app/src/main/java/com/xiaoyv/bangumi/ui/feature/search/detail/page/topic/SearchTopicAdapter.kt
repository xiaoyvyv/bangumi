package com.xiaoyv.bangumi.ui.feature.search.detail.page.topic

import android.graphics.Color
import androidx.core.view.isVisible
import com.xiaoyv.bangumi.databinding.ActivityCollectionItemBinding
import com.xiaoyv.bangumi.databinding.ActivitySearchDetailItemBinding
import com.xiaoyv.common.api.parser.entity.SearchResultEntity
import com.xiaoyv.common.api.response.SearchApiTopicEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.highlightText
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [SearchTopicAdapter]
 *
 * @author why
 * @since 12/8/23
 */
class SearchTopicAdapter(val keyword: () -> List<String>) :
    BaseQuickDiffBindingAdapter<SearchResultEntity,
            ActivityCollectionItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<ActivityCollectionItemBinding>.converted(item: SearchResultEntity) {
        (item.payload as SearchApiTopicEntity).let { entity ->
            binding.ivCover.isVisible = entity.image.orEmpty().isNotBlank()
            binding.ivCover.loadImageAnimate(entity.image)

            binding.tvTitle.text = entity.title
            binding.tvDesc.text = entity.summary.orEmpty()
            binding.tvDesc.isVisible = entity.summary.orEmpty().isNotBlank()
            binding.tvTime.text = entity.time

            binding.tvEp.text = "话题"

            // 高亮
            binding.tvTitle.highlightText(keyword(), Color.GREEN)
            binding.tvDesc.highlightText(keyword(), Color.GREEN)
        }
    }
}