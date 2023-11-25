package com.xiaoyv.bangumi.ui.media.type

import androidx.recyclerview.widget.DiffUtil
import com.xiaoyv.bangumi.databinding.FragmentMediaPageItemBinding
import com.xiaoyv.common.api.parser.entity.BrowserEntity
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [MediaPageAdapter]
 *
 * @author why
 * @since 11/25/23
 */
class MediaPageAdapter : BaseQuickDiffBindingAdapter<BrowserEntity.Item,
        FragmentMediaPageItemBinding>(TimelineDiffCallback) {

    override fun BaseQuickBindingHolder<FragmentMediaPageItemBinding>.converted(item: BrowserEntity.Item) {
        val infoTip = item.infoTip

        binding.ivCover.loadImageAnimate(item.coverImage)
        binding.tvTitle.text = item.titleCn
        binding.tvTag.text = infoTip.time.ifBlank { infoTip.eps }
        binding.tvSource.text = item.ratingScore
    }

    object TimelineDiffCallback : DiffUtil.ItemCallback<BrowserEntity.Item>() {
        override fun areItemsTheSame(
            oldItem: BrowserEntity.Item,
            newItem: BrowserEntity.Item
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: BrowserEntity.Item,
            newItem: BrowserEntity.Item
        ): Boolean {
            return oldItem == newItem
        }
    }
}