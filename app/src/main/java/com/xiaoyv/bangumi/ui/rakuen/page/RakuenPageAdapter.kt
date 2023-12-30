@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.ui.rakuen.page

import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.xiaoyv.bangumi.databinding.FragmentSuperPageItemBinding
import com.xiaoyv.common.api.parser.entity.SuperTopicEntity
import com.xiaoyv.common.config.annotation.TopicTimeType
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.tint
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.getAttrColor

/**
 * Class: [RakuenPageAdapter]
 *
 * @author why
 * @since 11/25/23
 */
class RakuenPageAdapter : BaseQuickDiffBindingAdapter<SuperTopicEntity,
        FragmentSuperPageItemBinding>(SuperTopicDiffCallback) {

    override fun BaseQuickBindingHolder<FragmentSuperPageItemBinding>.converted(item: SuperTopicEntity) {
        binding.ivAvatar.loadImageAnimate(item.avatarUrl, cropType = ImageView.ScaleType.FIT_START)
        binding.tvAttach.text = item.attachTitle
        binding.tvTime.text = item.time
        binding.ivAction.isVisible = item.canShowActionMenu
        binding.tvTitle.text = item.title

        binding.tvHot.isVisible = item.timeType.contains(TopicTimeType.TYPE_HOT)
//        binding.tvComment.text = String.format("评论：%s", item.commentCount)

        when {
            item.timeType.contains(TopicTimeType.TYPE_OLD) || item.timeType.contains(TopicTimeType.TYPE_OLDEST) -> {
                binding.tvTag.isVisible = true
                binding.tvTag.text =
                    if (item.timeType.contains(TopicTimeType.TYPE_OLD)) "旧贴" else "坟贴"
                binding.tvTag.backgroundTintList =
                    context.getAttrColor(GoogleAttr.colorOnSurface).tint
                binding.tvTag.setTextColor(context.getAttrColor(GoogleAttr.colorSurface))
            }

            item.timeType.contains(TopicTimeType.TYPE_NEW) -> {
                binding.tvTag.isVisible = true
                binding.tvTag.text = "新贴"
                binding.tvTag.backgroundTintList =
                    context.getAttrColor(GoogleAttr.colorPrimary).tint
                binding.tvTag.setTextColor(context.getAttrColor(GoogleAttr.colorOnPrimary))
            }

            else -> {
                binding.tvTag.isVisible = false
            }
        }
    }

    object SuperTopicDiffCallback : DiffUtil.ItemCallback<SuperTopicEntity>() {
        override fun areItemsTheSame(
            oldItem: SuperTopicEntity,
            newItem: SuperTopicEntity,
        ): Boolean {
            return oldItem.userName == newItem.userName && oldItem.title == newItem.title
        }

        override fun areContentsTheSame(
            oldItem: SuperTopicEntity,
            newItem: SuperTopicEntity,
        ): Boolean {
            return oldItem == newItem
        }
    }

}