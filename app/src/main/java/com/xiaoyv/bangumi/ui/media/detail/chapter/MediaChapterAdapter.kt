package com.xiaoyv.bangumi.ui.media.detail.chapter

import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.xiaoyv.bangumi.databinding.FragmentMediaChapterItemBinding
import com.xiaoyv.common.api.parser.entity.MediaChapterEntity
import com.xiaoyv.common.kts.CommonColor
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.tint
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.getAttrColor

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
        binding.vAired.text = item.stateText
       if (item.aired) {
           binding.vAired.backgroundTintList =   context.getColor(CommonColor.save_collect).tint
            binding.vAired.setTextColor(context.getAttrColor(GoogleAttr.colorOnPrimarySurface))
        } else {
           binding.vAired.backgroundTintList = context.getAttrColor(GoogleAttr.colorSurfaceContainer).tint
           binding.vAired.setTextColor(context.getAttrColor(GoogleAttr.colorOnSurfaceVariant))
        }
    }

    private object ItemDiffItemCallback : DiffUtil.ItemCallback<MediaChapterEntity>() {
        override fun areItemsTheSame(
            oldItem: MediaChapterEntity,
            newItem: MediaChapterEntity,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: MediaChapterEntity,
            newItem: MediaChapterEntity,
        ): Boolean {
            return oldItem == newItem
        }
    }
}