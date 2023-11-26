@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.ui.rakuen.page

import androidx.recyclerview.widget.DiffUtil
import com.xiaoyv.bangumi.databinding.FragmentSuperPageItemBinding
import com.xiaoyv.common.api.parser.entity.SuperTopicEntity
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

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
        binding.tvTitle.text = item.title
        binding.tvAttach.text = item.attachTitle
        binding.tvTime.text = item.time
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