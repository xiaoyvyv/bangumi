package com.xiaoyv.bangumi.ui.media.detail.chapter

import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.xiaoyv.bangumi.databinding.FragmentMediaChapterItemBinding
import com.xiaoyv.common.api.parser.entity.MediaChapterEntity
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [MediaChapterAdapter]
 *
 * @author why
 * @since 11/24/23
 */
class MediaChapterAdapter : BaseQuickDiffBindingAdapter<MediaChapterEntity,
        FragmentMediaChapterItemBinding>(ItemDiffItemCallback) {

    override fun BaseQuickBindingHolder<FragmentMediaChapterItemBinding>.converted(item: MediaChapterEntity) {
        binding.titleNative.text = item.titleNative
        binding.titleCn.text = item.titleCn
        binding.titleCn.isVisible = item.titleCn.isNotBlank()
        binding.tvTime.text = item.time
        binding.tvComment.text = String.format("讨论：%d", item.commentCount)
    }

    private object ItemDiffItemCallback : DiffUtil.ItemCallback<MediaChapterEntity>() {
        override fun areItemsTheSame(
            oldItem: MediaChapterEntity,
            newItem: MediaChapterEntity
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: MediaChapterEntity,
            newItem: MediaChapterEntity
        ): Boolean {
            return oldItem == newItem
        }
    }
}