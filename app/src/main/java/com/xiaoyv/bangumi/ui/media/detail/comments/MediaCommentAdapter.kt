package com.xiaoyv.bangumi.ui.media.detail.comments

import androidx.recyclerview.widget.DiffUtil
import com.xiaoyv.bangumi.databinding.FragmentMediaCommentItemBinding
import com.xiaoyv.common.api.parser.entity.MediaCommentEntity
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [MediaCommentAdapter]
 *
 * @author why
 * @since 11/24/23
 */
class MediaCommentAdapter : BaseQuickDiffBindingAdapter<MediaCommentEntity,
        FragmentMediaCommentItemBinding>(ItemDiffItemCallback) {

    override fun BaseQuickBindingHolder<FragmentMediaCommentItemBinding>.converted(item: MediaCommentEntity) {
        binding.ivAvatar.loadImageAnimate(item.avatar)
        binding.tvAttach.text = item.comment
        binding.tvTitle.text = item.userName
        binding.tvTime.text = item.time
        binding.ivStar.rating = item.star
    }

    private object ItemDiffItemCallback : DiffUtil.ItemCallback<MediaCommentEntity>() {
        override fun areItemsTheSame(
            oldItem: MediaCommentEntity,
            newItem: MediaCommentEntity
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: MediaCommentEntity,
            newItem: MediaCommentEntity
        ): Boolean {
            return oldItem == newItem
        }
    }

}