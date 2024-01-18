package com.xiaoyv.bangumi.ui.feature.search.detail.page.tag

import com.xiaoyv.bangumi.databinding.ActivitySearchDetailTagBinding
import com.xiaoyv.common.api.parser.entity.SearchResultEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [SearchTagAdapter]
 *
 * @author why
 * @since 12/8/23
 */
class SearchTagAdapter : BaseQuickDiffBindingAdapter<SearchResultEntity,
        ActivitySearchDetailTagBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<ActivitySearchDetailTagBinding>.converted(item: SearchResultEntity) {
        binding.tvTitleTag.text = item.title
        binding.tvCommentCount.text = item.count
    }
}