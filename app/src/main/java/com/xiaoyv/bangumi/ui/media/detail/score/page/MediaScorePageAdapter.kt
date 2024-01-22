package com.xiaoyv.bangumi.ui.media.detail.score.page

import androidx.core.view.isVisible
import com.xiaoyv.bangumi.databinding.FragmentMediaScoreItemBinding
import com.xiaoyv.common.api.parser.entity.MediaScoreEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [MediaScorePageAdapter]
 *
 * @author why
 * @since 1/22/24
 */
class MediaScorePageAdapter : BaseQuickDiffBindingAdapter<MediaScoreEntity,
        FragmentMediaScoreItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<FragmentMediaScoreItemBinding>.converted(item: MediaScoreEntity) {
        binding.ivAvatar.loadImageAnimate(item.avatar)
        binding.tvTitle.text = item.name
        binding.tvTime.text = item.time
        binding.ivStar.rating = item.score / 2f
        binding.tvContent.text = item.comment
        binding.tvContent.isVisible = item.comment.isNotBlank()
    }
}