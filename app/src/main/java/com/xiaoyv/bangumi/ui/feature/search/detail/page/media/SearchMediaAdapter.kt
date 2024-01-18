package com.xiaoyv.bangumi.ui.feature.search.detail.page.media

import android.widget.ImageView
import androidx.core.view.isVisible
import com.xiaoyv.bangumi.databinding.ActivitySearchDetailItemBinding
import com.xiaoyv.common.api.parser.entity.SearchResultEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [SearchMediaAdapter]
 *
 * @author why
 * @since 12/8/23
 */
class SearchMediaAdapter : BaseQuickDiffBindingAdapter<SearchResultEntity,
        ActivitySearchDetailItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<ActivitySearchDetailItemBinding>.converted(item: SearchResultEntity) {
        binding.ivCover.loadImageAnimate(item.coverImage, cropType = ImageView.ScaleType.FIT_START)
        binding.tvTitle.text = item.title
        binding.tvTitleCn.text = item.subtitle
        binding.tvDesc.text = item.infoTip.toString()
        binding.tvCommentCount.text = item.count
        binding.ivStar.rating = item.rating / 2f
        binding.ivStar.isVisible = item.rating != 0f
        binding.tvRank.text = item.rank
        binding.tvRank.isVisible = item.rank.isNotBlank()

        if (item.infoTip.time.isNotBlank()) {
            binding.tvTime.text = buildString {
                append(item.searchTip)
                append(" ")
                append(item.infoTip.time)
            }
        } else {
            binding.tvTime.text = item.searchTip
        }
    }
}