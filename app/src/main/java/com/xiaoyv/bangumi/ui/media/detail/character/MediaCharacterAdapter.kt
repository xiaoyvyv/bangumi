package com.xiaoyv.bangumi.ui.media.detail.character

import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.xiaoyv.bangumi.databinding.FragmentMediaCharacterItemBinding
import com.xiaoyv.common.api.parser.entity.MediaCharacterEntity
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [MediaCharacterAdapter]
 *
 * @author why
 * @since 11/24/23
 */
class MediaCharacterAdapter : BaseQuickDiffBindingAdapter<MediaCharacterEntity,
        FragmentMediaCharacterItemBinding>(ItemDiffItemCallback) {

    override fun BaseQuickBindingHolder<FragmentMediaCharacterItemBinding>.converted(item: MediaCharacterEntity) {
        binding.ivAvatar.loadImageAnimate(item.avatar, cropType = ImageView.ScaleType.FIT_START)
        binding.tvTitle.text = String.format("%s/%s", item.titleNative, item.titleCn)
        binding.tvComment.text = String.format("讨论：%s", item.commentCount)
        binding.tvComment.isVisible = item.commentCount.isNotBlank()
        binding.tvJob.text = buildString {
            append(item.personJob)
            append(";")
            append(item.personSex)
        }
    }

    private object ItemDiffItemCallback : DiffUtil.ItemCallback<MediaCharacterEntity>() {
        override fun areItemsTheSame(
            oldItem: MediaCharacterEntity,
            newItem: MediaCharacterEntity,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: MediaCharacterEntity,
            newItem: MediaCharacterEntity,
        ): Boolean {
            return oldItem == newItem
        }
    }
}