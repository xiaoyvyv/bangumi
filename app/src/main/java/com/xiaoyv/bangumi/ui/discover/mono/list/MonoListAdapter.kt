package com.xiaoyv.bangumi.ui.discover.mono.list

import android.widget.ImageView
import com.xiaoyv.bangumi.databinding.FragmentOverviewCharacterItemBinding
import com.xiaoyv.common.config.bean.SampleImageEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [MonoListAdapter]
 *
 * @author why
 * @since 12/21/23
 */
class MonoListAdapter : BaseQuickDiffBindingAdapter<SampleImageEntity,
        FragmentOverviewCharacterItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<FragmentOverviewCharacterItemBinding>.converted(item: SampleImageEntity) {
        binding.ivAvatar.loadImageAnimate(item.image, cropType = ImageView.ScaleType.FIT_START)
        binding.tvName.text = item.title
        binding.tvPerson.text = item.desc
    }
}