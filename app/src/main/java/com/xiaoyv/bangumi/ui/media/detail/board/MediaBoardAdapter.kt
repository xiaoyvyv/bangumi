package com.xiaoyv.bangumi.ui.media.detail.board

import androidx.recyclerview.widget.DiffUtil
import com.xiaoyv.bangumi.databinding.FragmentMediaBoardItemBinding
import com.xiaoyv.common.api.parser.entity.MediaBoardEntity
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [MediaBoardAdapter]
 *
 * @author why
 * @since 11/24/23
 */
class MediaBoardAdapter : BaseQuickDiffBindingAdapter<MediaBoardEntity,
        FragmentMediaBoardItemBinding>(MediaBoardDiffItemCallback) {

    override fun BaseQuickBindingHolder<FragmentMediaBoardItemBinding>.converted(item: MediaBoardEntity) {
        binding.tvTitle.text = item.userName
        binding.tvContent.text = item.content
        binding.tvReplay.text = item.replies
        binding.tvTime.text = item.time
    }

    object MediaBoardDiffItemCallback : DiffUtil.ItemCallback<MediaBoardEntity>() {
        override fun areItemsTheSame(
            oldItem: MediaBoardEntity,
            newItem: MediaBoardEntity
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: MediaBoardEntity,
            newItem: MediaBoardEntity
        ): Boolean {
            return oldItem == newItem
        }
    }
}