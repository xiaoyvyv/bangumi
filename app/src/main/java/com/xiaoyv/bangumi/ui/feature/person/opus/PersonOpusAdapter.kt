package com.xiaoyv.bangumi.ui.feature.person.opus

import androidx.recyclerview.widget.DiffUtil
import com.xiaoyv.bangumi.databinding.FragmentPersonCollectItemBinding
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
class PersonOpusAdapter : BaseQuickDiffBindingAdapter<PersonEntity.RecentCooperate,
        FragmentPersonCollectItemBinding>(ItemDiffItemCallback) {

    override fun BaseQuickBindingHolder<FragmentPersonCollectItemBinding>.converted(item: PersonEntity.RecentCooperate) {
        binding.ivAvatar.loadImageAnimate(item.avatar)
        binding.tvTip.text = item.name
    }

    private object ItemDiffItemCallback : DiffUtil.ItemCallback<PersonEntity.RecentCooperate>() {
        override fun areItemsTheSame(
            oldItem: PersonEntity.RecentCooperate,
            newItem: PersonEntity.RecentCooperate
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: PersonEntity.RecentCooperate,
            newItem: PersonEntity.RecentCooperate
        ): Boolean {
            return oldItem == newItem
        }
    }
}