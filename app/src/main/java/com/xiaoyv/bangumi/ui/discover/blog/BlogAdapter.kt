package com.xiaoyv.bangumi.ui.discover.blog

import androidx.recyclerview.widget.DiffUtil
import com.xiaoyv.bangumi.databinding.FragmentBlogItemBinding
import com.xiaoyv.common.api.parser.entity.BlogEntity
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [BlogAdapter]
 *
 * @author why
 * @since 11/24/23
 */
class BlogAdapter : BaseQuickDiffBindingAdapter<BlogEntity,
        FragmentBlogItemBinding>(BlogDiffCallback) {

    override fun BaseQuickBindingHolder<FragmentBlogItemBinding>.converted(item: BlogEntity) {
        binding.ivCover.loadImageAnimate(item.image, holder = true)
        binding.tvTag.text = item.title
        binding.tvTitle.text = item.title
        binding.tvDesc.text = item.content
        binding.tvTime.text = item.time
        binding.tvTimeline.text = item.timeline
    }

    object BlogDiffCallback : DiffUtil.ItemCallback<BlogEntity>() {
        override fun areItemsTheSame(
            oldItem: BlogEntity,
            newItem: BlogEntity
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: BlogEntity,
            newItem: BlogEntity
        ): Boolean {
            return oldItem == newItem
        }
    }
}