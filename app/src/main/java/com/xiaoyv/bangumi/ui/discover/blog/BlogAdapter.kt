package com.xiaoyv.bangumi.ui.discover.blog

import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.blankj.utilcode.util.SpanUtils
import com.xiaoyv.bangumi.databinding.FragmentBlogItemBinding
import com.xiaoyv.common.api.parser.entity.BlogEntity
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.getAttrColor

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
        binding.tvTitle.text = item.title
        binding.tvDesc.text = item.content
        binding.tvTime.text = item.time
        binding.tvRecent.isVisible = item.recentUserName.isNotBlank()
        binding.tvCommentCount.text = String.format("讨论：+%d", item.commentCount)

        SpanUtils.with(binding.tvRecent)
            .append(item.recentUserName)
            .setForegroundColor(context.getAttrColor(GoogleAttr.colorPrimary))
            .append("：发表了该日志")
            .create()
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