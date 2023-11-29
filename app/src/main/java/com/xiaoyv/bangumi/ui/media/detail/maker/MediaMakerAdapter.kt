package com.xiaoyv.bangumi.ui.media.detail.maker

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
        binding.tvTitle.text = item.titleNative + "/" + item.titleCn
        binding.tvJob.text = item.personInfo.joinToString(";")
        binding.tvComment.text = item.commentCount
        binding.tvTitle.text = item.tip
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