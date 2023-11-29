package com.xiaoyv.bangumi.ui.media.detail.review

import androidx.recyclerview.widget.DiffUtil
import com.xiaoyv.bangumi.databinding.FragmentMediaReviewItemBinding
import com.xiaoyv.common.api.parser.entity.MediaReviewEntity
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [MediaReviewAdapter]
 *
 * @author why
 * @since 11/24/23
 */
class MediaReviewAdapter : BaseQuickDiffBindingAdapter<MediaReviewEntity,
        FragmentMediaReviewItemBinding>(MediaReviewDiffItemCallback) {

    override fun BaseQuickBindingHolder<FragmentMediaReviewItemBinding>.converted(item: MediaReviewEntity) {
        binding.ivAvatar.loadImageAnimate(item.avatar)
        binding.tvTitle.text = "日志：" + item.title
        binding.tvContent.text = item.comment
        binding.tvTime.text = item.time
    }

    object MediaReviewDiffItemCallback : DiffUtil.ItemCallback<MediaReviewEntity>() {
        override fun areItemsTheSame(
            oldItem: MediaReviewEntity,
            newItem: MediaReviewEntity
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: MediaReviewEntity,
            newItem: MediaReviewEntity
        ): Boolean {
            return oldItem == newItem
        }
    }
}