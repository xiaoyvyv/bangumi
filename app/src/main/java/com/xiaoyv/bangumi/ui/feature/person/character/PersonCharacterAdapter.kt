package com.xiaoyv.bangumi.ui.feature.person.character

import androidx.recyclerview.widget.DiffUtil
import com.xiaoyv.bangumi.databinding.FragmentMediaBoardItemBinding
import com.xiaoyv.common.api.parser.entity.MediaBoardEntity
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [PersonCharacterAdapter]
 *
 * @author why
 * @since 12/5/23
 */
class PersonCharacterAdapter : BaseQuickDiffBindingAdapter<MediaBoardEntity,
        FragmentMediaBoardItemBinding>(ItemDiffItemCallback) {

    override fun BaseQuickBindingHolder<FragmentMediaBoardItemBinding>.converted(item: MediaBoardEntity) {
        binding.tvTitle.text = String.format("用户：%s", item.userName)
        binding.tvContent.text = item.content
        binding.tvReplay.text = item.replies
        binding.tvTime.text = item.time
    }

    private object ItemDiffItemCallback : DiffUtil.ItemCallback<MediaBoardEntity>() {
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