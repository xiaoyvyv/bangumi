package com.xiaoyv.bangumi.ui.feature.person.character

import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.xiaoyv.bangumi.databinding.FragmentMediaPageItemBinding
import com.xiaoyv.common.api.parser.entity.CharacterEntity
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [PersonCharacterAdapter]
 *
 * @author why
 * @since 12/5/23
 */
class PersonCharacterAdapter : BaseQuickDiffBindingAdapter<CharacterEntity,
        FragmentMediaPageItemBinding>(ItemDiffItemCallback) {

    override fun BaseQuickBindingHolder<FragmentMediaPageItemBinding>.converted(item: CharacterEntity) {
        binding.ivCover.loadImageAnimate(item.avatar)
        binding.tvTitle.text = item.nameCn.ifBlank { item.nameNative }
        binding.tvTag.text = String.format("x%d", item.from.size)
        binding.tvSource.isVisible = false
    }

    private object ItemDiffItemCallback : DiffUtil.ItemCallback<CharacterEntity>() {
        override fun areItemsTheSame(
            oldItem: CharacterEntity,
            newItem: CharacterEntity
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: CharacterEntity,
            newItem: CharacterEntity
        ): Boolean {
            return oldItem == newItem
        }
    }
}