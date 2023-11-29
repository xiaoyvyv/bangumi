package com.xiaoyv.bangumi.ui.media.detail.maker

import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.xiaoyv.bangumi.databinding.FragmentMediaMakerItemBinding
import com.xiaoyv.common.api.parser.entity.MediaMakerEntity
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [MediaMakerAdapter]
 *
 * @author why
 * @since 11/24/23
 */
class MediaMakerAdapter : BaseQuickDiffBindingAdapter<MediaMakerEntity,
        FragmentMediaMakerItemBinding>(ItemDiffItemCallback) {

    override fun BaseQuickBindingHolder<FragmentMediaMakerItemBinding>.converted(item: MediaMakerEntity) {
        binding.ivAvatar.loadImageAnimate(item.avatar)
        binding.tvTitle.text = String.format("%s/%s", item.titleNative, item.titleCn)
        binding.tvJob.text = item.personInfo.joinToString(";")
        binding.tvComment.text = String.format("讨论：%s", item.commentCount)
        binding.tvComment.isVisible = item.commentCount.isNotBlank()
        binding.tvTip.text = item.tip
        binding.tvTip.isVisible = item.tip.isNotBlank()
    }

    private object ItemDiffItemCallback : DiffUtil.ItemCallback<MediaMakerEntity>() {
        override fun areItemsTheSame(
            oldItem: MediaMakerEntity,
            newItem: MediaMakerEntity
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: MediaMakerEntity,
            newItem: MediaMakerEntity
        ): Boolean {
            return oldItem == newItem
        }
    }
}