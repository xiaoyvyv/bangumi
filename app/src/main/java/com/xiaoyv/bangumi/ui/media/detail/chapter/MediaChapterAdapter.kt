package com.xiaoyv.bangumi.ui.media.detail.chapter

import android.graphics.Color
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.xiaoyv.bangumi.databinding.FragmentMediaChapterItemBinding
import com.xiaoyv.common.api.parser.entity.MediaChapterEntity
import com.xiaoyv.common.config.annotation.InterestType
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
        if (item.splitter) {
            binding.titleNative.text = item.number
            binding.titleNative.setTextColor(context.getAttrColor(GoogleAttr.colorOnSurface))
        } else {
            binding.titleNative.text = item.titleNative
            binding.titleNative.setTextColor(context.getAttrColor(GoogleAttr.colorPrimary))
        }

        binding.titleCn.text = item.titleCn
        binding.titleCn.isVisible = item.titleCn.isNotBlank()

        binding.tvTime.text = item.time
        binding.tvTime.isVisible = item.splitter.not()

        binding.tvComment.text = String.format("讨论：%d", item.commentCount)
        binding.tvComment.isVisible = item.splitter.not()

        binding.vDivider.isVisible = item.splitter.not()
        binding.vAired.isVisible = item.airedStateText.isNotBlank()
        when {
            // 分割
            item.splitter -> {
                binding.vAired.text = null
                binding.vAired.backgroundTintList = Color.TRANSPARENT.tint
                binding.vAired.setTextColor(Color.TRANSPARENT)
            }
            // 看过
            item.collectType == InterestType.TYPE_COLLECT -> {
                binding.vAired.text = item.collectStateText
                binding.vAired.backgroundTintList = context.getColor(CommonColor.save_collect).tint
                binding.vAired.setTextColor(context.getColor(CommonColor.save_collect_text))
            }
            // 想看
            item.collectType == InterestType.TYPE_WISH -> {
                binding.vAired.text = item.collectStateText
                binding.vAired.backgroundTintList = context.getColor(CommonColor.save_wish).tint
                binding.vAired.setTextColor(context.getColor(CommonColor.save_wish_text))
            }
            // 抛弃
            item.collectType == InterestType.TYPE_DROPPED -> {
                binding.vAired.text = item.collectStateText
                binding.vAired.backgroundTintList = context.getColor(CommonColor.save_dropped).tint
                binding.vAired.setTextColor(context.getColor(CommonColor.save_dropped_text))
            }
            // 放送中
            item.isAiring -> {
                binding.vAired.text = item.airedStateText
                binding.vAired.backgroundTintList = context.getColor(CommonColor.state_airing).tint
                binding.vAired.setTextColor(context.getColor(CommonColor.state_airing_text))
            }
            // 已经播出
            item.isAired -> {
                binding.vAired.text = item.airedStateText
                binding.vAired.backgroundTintList = context.getColor(CommonColor.state_aired).tint
                binding.vAired.setTextColor(context.getColor(CommonColor.state_aired_text))
            }
            // 未播出
            else -> {
                binding.vAired.text = item.airedStateText
                binding.vAired.backgroundTintList =
                    context.getAttrColor(GoogleAttr.colorSurfaceContainer).tint
                binding.vAired.setTextColor(context.getAttrColor(GoogleAttr.colorOnSurfaceVariant))
            }
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