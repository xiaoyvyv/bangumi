@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.ui.rakuen.page

import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.blankj.utilcode.util.SpanUtils
import com.xiaoyv.bangumi.databinding.FragmentSuperPageItemBinding
import com.xiaoyv.common.api.parser.entity.SuperTopicEntity
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.dpi
import com.xiaoyv.widget.kts.getAttrColor
import com.xiaoyv.widget.kts.spi

/**
 * Class: [RakuenPageAdapter]
 *
 * @author why
 * @since 11/25/23
 */
class RakuenPageAdapter : BaseQuickDiffBindingAdapter<SuperTopicEntity,
        FragmentSuperPageItemBinding>(SuperTopicDiffCallback) {

    override fun BaseQuickBindingHolder<FragmentSuperPageItemBinding>.converted(item: SuperTopicEntity) {
        binding.ivAvatar.loadImageAnimate(item.avatarUrl)
        binding.tvAttach.text = item.attachTitle
        binding.tvTime.text = item.time
        binding.ivAction.isVisible = item.canShowActionMenu

        SpanUtils.with(binding.tvTitle)
            .append(item.title)
            .appendSpace(4.dpi)
            .append(String.format("+%s", item.commentCount))
            .setForegroundColor(context.getAttrColor(GoogleAttr.colorPrimary))
            .setFontSize(12.spi)
            .create()
    }

    object SuperTopicDiffCallback : DiffUtil.ItemCallback<SuperTopicEntity>() {
        override fun areItemsTheSame(
            oldItem: SuperTopicEntity,
            newItem: SuperTopicEntity
        ): Boolean {
            return oldItem.userName == newItem.userName && oldItem.title == newItem.title
        }

        override fun areContentsTheSame(
            oldItem: SuperTopicEntity,
            newItem: SuperTopicEntity
        ): Boolean {
            return oldItem == newItem
        }
    }

}