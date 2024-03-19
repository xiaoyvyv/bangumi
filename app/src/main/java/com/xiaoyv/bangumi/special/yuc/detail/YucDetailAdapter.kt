package com.xiaoyv.bangumi.special.yuc.detail

import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.xiaoyv.bangumi.databinding.ActivityYucDetailItemBinding
import com.xiaoyv.common.api.parser.entity.BrowserEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.IMAGE_HOLDER_3X4
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [YucDetailAdapter]
 *
 * @author why
 * @since 11/25/23
 */
class YucDetailAdapter : BaseQuickDiffBindingAdapter<BrowserEntity.Item,
        ActivityYucDetailItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<ActivityYucDetailItemBinding>.converted(item: BrowserEntity.Item) {
        binding.ivCover.loadImageAnimate(item.coverImage, holderType = IMAGE_HOLDER_3X4)
        binding.tvTitle.text = item.title
        binding.tvDesc.text = item.subtitle

        binding.tvSource.isInvisible = item.ratingScore.isBlank()
        binding.tvSource.text = item.ratingScore

        binding.ivStar.rating = item.rating / 2f
        binding.ivStar.isVisible = item.rating != 0f

        binding.tvRank.isInvisible = item.rank.isBlank()
        binding.tvRank.text = item.rank

        binding.tvTag.text = item.infoTip.monthDay
            .ifBlank { item.infoTip.yearMonth }
            .ifBlank { item.infoTip.eps }
    }
}