package com.xiaoyv.bangumi.ui.feature.friendly.rank

import androidx.core.view.isVisible
import com.xiaoyv.bangumi.databinding.FragmentMediaPageItemBinding
import com.xiaoyv.common.database.friendly.FriendlyRankEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.IMAGE_HOLDER_3X4
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [FriendRankDetailAdapter]
 *
 * @author why
 * @since 11/25/23
 */
class FriendRankDetailAdapter :
    BaseQuickDiffBindingAdapter<FriendlyRankEntity, FragmentMediaPageItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<FragmentMediaPageItemBinding>.converted(item: FriendlyRankEntity) {
        binding.ivCover.loadImageAnimate(item.subjectCover, holderType = IMAGE_HOLDER_3X4)

        binding.tvTitle.text = item.subjectNameCn.orEmpty().ifBlank { item.subjectName }
        binding.tvSource.text = String.format("%.1f", item.friendlyRate)
        binding.tvRank.isVisible = true
        binding.tvRank.text = String.format("Rank: %d", bindingAdapterPosition + 1)
        binding.tvTag.text = item.subjectDate
    }
}