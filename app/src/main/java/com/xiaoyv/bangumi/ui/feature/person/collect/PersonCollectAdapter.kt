package com.xiaoyv.bangumi.ui.feature.person.collect

import com.xiaoyv.bangumi.databinding.FragmentPersonCollectItemBinding
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.helper.IdDiffItemCallback
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [PersonCollectAdapter]
 *
 * @author why
 * @since 12/5/23
 */
class PersonCollectAdapter : BaseQuickDiffBindingAdapter<MediaDetailEntity.MediaWho,
        FragmentPersonCollectItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<FragmentPersonCollectItemBinding>.converted(item: MediaDetailEntity.MediaWho) {
        binding.ivAvatar.loadImageAnimate(item.avatar)
        binding.tvTip.text = item.name
    }
}