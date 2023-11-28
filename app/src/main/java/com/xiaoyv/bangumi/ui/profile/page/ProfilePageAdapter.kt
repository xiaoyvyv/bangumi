package com.xiaoyv.bangumi.ui.profile.page

import androidx.core.text.parseAsHtml
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.xiaoyv.bangumi.databinding.FragmentTimelinePageItemBinding
import com.xiaoyv.common.api.parser.entity.TimelineEntity
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [ProfilePageAdapter]
 *
 * @author why
 * @since 11/25/23
 */
class ProfilePageAdapter : BaseQuickDiffBindingAdapter<TimelineEntity,
        FragmentTimelinePageItemBinding>(TimelineDiffCallback) {

    override fun BaseQuickBindingHolder<FragmentTimelinePageItemBinding>.converted(item: TimelineEntity) {
        val itemCard = item.card
        val character = item.character
        val images = item.images
        val collectInfo = item.collectInfo

        binding.ivCharacter.isVisible = character != null
        binding.ivCharacter.loadImageAnimate(character?.avatar)
        binding.ivAvatar.isInvisible = item.avatar.isBlank()
        binding.ivAvatar.loadImageAnimate(item.avatar)
        binding.tvAction.text = item.userActionText
        binding.tvTime.text = item.timeText

        if (itemCard == null) {
            binding.tvCardContent.isVisible = false
            binding.tvCardRate.isVisible = false
            binding.ivCardCover.isVisible = false
        } else {
            binding.tvCardContent.isVisible = true
            binding.tvCardRate.isVisible = true
            binding.ivCardCover.isVisible = true
            binding.tvCardContent.text = itemCard.title
            binding.tvCardRate.text = buildString {
                append(itemCard.cardRate)
                append(" ")
                append(itemCard.cardRateTotal)
            }
            binding.ivCardCover.loadImageAnimate(itemCard.coverUrl)
        }

        if (images.isNotEmpty()) {
            binding.imageGrid.isVisible = images.isNotEmpty()
            binding.imageGrid.images = images
        } else {
            binding.imageGrid.isVisible = false
        }

        if (collectInfo != null) {
            binding.rating.isVisible = true
            binding.rating.data = collectInfo
        } else {
            binding.rating.isVisible = false
        }
    }

    object TimelineDiffCallback : DiffUtil.ItemCallback<TimelineEntity>() {
        override fun areItemsTheSame(oldItem: TimelineEntity, newItem: TimelineEntity): Boolean {
            return oldItem.userActionText == newItem.userActionText
        }

        override fun areContentsTheSame(oldItem: TimelineEntity, newItem: TimelineEntity): Boolean {
            return oldItem == newItem
        }
    }
}