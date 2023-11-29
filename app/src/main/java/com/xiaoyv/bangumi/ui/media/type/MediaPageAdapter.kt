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
        FragmentMediaPageItemBinding>(BrowserListDiffCallback) {

    override fun BaseQuickBindingHolder<FragmentMediaPageItemBinding>.converted(item: BrowserEntity.Item) {
        binding.ivCover.loadImageAnimate(item.coverImage, holder = true)
        binding.tvTitle.text = item.title
        binding.tvSource.text = item.ratingScore

        item.infoTip.apply {
            binding.tvTag.text = time.ifBlank { eps }
        }
    }

    object BrowserListDiffCallback : DiffUtil.ItemCallback<BrowserEntity.Item>() {
        override fun areItemsTheSame(
            oldItem: BrowserEntity.Item,
            newItem: BrowserEntity.Item
        ): Boolean {
            return oldItem.subjectId == newItem.subjectId
        }

        override fun areContentsTheSame(
            oldItem: BrowserEntity.Item,
            newItem: BrowserEntity.Item
        ): Boolean {
            return oldItem == newItem
        }
    }
}