package com.xiaoyv.bangumi.ui.feature.person.character

import android.widget.ImageView
import androidx.core.view.isVisible
import com.xiaoyv.bangumi.databinding.FragmentMediaPageItemBinding
import com.xiaoyv.common.api.parser.entity.CharacterEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
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
        FragmentMediaPageItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<FragmentMediaPageItemBinding>.converted(item: CharacterEntity) {
        binding.ivCover.loadImageAnimate(item.avatar, cropType = ImageView.ScaleType.FIT_START)
        binding.tvTitle.text = item.nameCn.ifBlank { item.nameNative }
        binding.tvTag.text = String.format("x%d", item.from.size)
        binding.tvSource.isVisible = false
    }
}