package com.xiaoyv.bangumi.ui.feature.person.opus

import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.xiaoyv.bangumi.databinding.FragmentMediaPageItemBinding
import com.xiaoyv.common.api.parser.entity.PersonEntity
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [PersonOpusAdapter]
 *
 * @author why
 * @since 12/5/23
 */
class PersonOpusAdapter : BaseQuickDiffBindingAdapter<PersonEntity.RecentlyOpus,
        FragmentMediaPageItemBinding>(ItemDiffItemCallback) {

    override fun BaseQuickBindingHolder<FragmentMediaPageItemBinding>.converted(item: PersonEntity.RecentlyOpus) {
        binding.ivCover.loadImageAnimate(item.cover)
        binding.tvTitle.text = item.titleCn
        binding.tvTag.text = item.jobs.joinToString("、")
        binding.tvSource.text = String.format("%.1f", item.rateInfo.rate)
        binding.tvSource.isVisible = item.rateInfo.rate != 0f
    }

    private object ItemDiffItemCallback : DiffUtil.ItemCallback<PersonEntity.RecentlyOpus>() {
        override fun areItemsTheSame(
            oldItem: PersonEntity.RecentlyOpus,
            newItem: PersonEntity.RecentlyOpus
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: PersonEntity.RecentlyOpus,
            newItem: PersonEntity.RecentlyOpus
        ): Boolean {
            return oldItem == newItem
        }
    }
}