package com.xiaoyv.bangumi.ui.media.type

import android.content.Context
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.xiaoyv.bangumi.databinding.FragmentMediaPageItemBinding
import com.xiaoyv.common.api.parser.entity.BrowserEntity
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.IMAGE_HOLDER_1X1
import com.xiaoyv.common.kts.IMAGE_HOLDER_3X4
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [MediaPageAdapter]
 *
 * @author why
 * @since 11/25/23
 */
class MediaPageAdapter(@MediaType private val mediaType: String) :
    BaseQuickDiffBindingAdapter<BrowserEntity.Item,
            FragmentMediaPageItemBinding>(IdDiffItemCallback()) {

    private val isMusic get() = mediaType == MediaType.TYPE_MUSIC

    override fun BaseQuickBindingHolder<FragmentMediaPageItemBinding>.converted(item: BrowserEntity.Item) {
        binding.ivCover.loadImageAnimate(
            item.coverImage,
            holderType = if (isMusic) IMAGE_HOLDER_1X1 else IMAGE_HOLDER_3X4
        )
        binding.tvTitle.text = item.title
        binding.tvSource.text = item.ratingScore
        binding.tvRank.isInvisible = item.rank.isBlank()
        binding.tvRank.text = item.rank
        binding.tvCollect.isVisible = item.isCollection

        item.infoTip.apply {
            binding.tvTag.text = yearMonth.ifBlank { eps }
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
    ): BaseQuickBindingHolder<FragmentMediaPageItemBinding> {
        return super.onCreateViewHolder(context, parent, viewType).apply {
            if (isMusic) {
                binding.ivCover.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    dimensionRatio = "1:1"
                }
            }
        }
    }
}