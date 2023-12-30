package com.xiaoyv.bangumi.ui.media.detail.review

import androidx.recyclerview.widget.DiffUtil
import com.xiaoyv.bangumi.databinding.FragmentMediaReviewItemBinding
import com.xiaoyv.common.api.parser.entity.MediaReviewBlogEntity
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [MediaReviewAdapter]
 *
 * @author why
 * @since 11/24/23
 */
class MediaReviewAdapter : BaseQuickDiffBindingAdapter<MediaReviewBlogEntity,
        FragmentMediaReviewItemBinding>(ItemDiffItemCallback) {

    override fun BaseQuickBindingHolder<FragmentMediaReviewItemBinding>.converted(item: MediaReviewBlogEntity) {
        binding.ivAvatar.loadImageAnimate(item.avatar)
        binding.tvTitle.text = item.title
        binding.tvContent.text = item.comment
        binding.tvTime.text = item.time
    }

    private object ItemDiffItemCallback : DiffUtil.ItemCallback<MediaReviewBlogEntity>() {
        override fun areItemsTheSame(
            oldItem: MediaReviewBlogEntity,
            newItem: MediaReviewBlogEntity,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: MediaReviewBlogEntity,
            newItem: MediaReviewBlogEntity,
        ): Boolean {
            return oldItem == newItem
        }
    }
}