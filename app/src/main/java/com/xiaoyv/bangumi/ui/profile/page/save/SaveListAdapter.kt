package com.xiaoyv.bangumi.ui.profile.page.save

import androidx.core.view.isVisible
import com.xiaoyv.bangumi.databinding.FragmentSaveListItemBinding
import com.xiaoyv.common.api.parser.entity.BrowserEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.IMAGE_HOLDER_3X4
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [SaveListAdapter]
 *
 * @author why
 * @since 11/24/23
 */
class SaveListAdapter : BaseQuickDiffBindingAdapter<BrowserEntity.Item,
        FragmentSaveListItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<FragmentSaveListItemBinding>.converted(item: BrowserEntity.Item) {
        val infoTip = item.infoTip

        binding.ivCover.loadImageAnimate(item.coverImage, holderType = IMAGE_HOLDER_3X4)
        binding.tvTitle.text = item.title
        binding.tvTag.text = item.mediaTypeName
        binding.tvDesc.text = infoTip.toString().ifBlank { item.subtitle }
        binding.tvTime.text = item.collectTime
        binding.tvEp.text = item.infoTip.eps
        binding.tvEp.isVisible = item.infoTip.eps.isNotBlank()
        binding.ivStar.rating = item.rating / 2f
        binding.ivStar.isVisible = item.rating != 0f
        binding.tvComment.text = item.collectComment
        binding.tvComment.isVisible = item.collectComment.isNotBlank()

        binding.tvSource.isVisible = false
    }
}