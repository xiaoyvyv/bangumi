package com.xiaoyv.bangumi.ui.feature.person.cooperate

import com.xiaoyv.bangumi.databinding.FragmentPersonCooperateItemBinding
import com.xiaoyv.common.api.parser.entity.PersonEntity
import com.xiaoyv.common.helper.IdDiffItemCallback
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [PersonCooperateAdapter]
 *
 * @author why
 * @since 12/5/23
 */
class PersonCooperateAdapter : BaseQuickDiffBindingAdapter<PersonEntity.RecentCooperate,
        FragmentPersonCooperateItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<FragmentPersonCooperateItemBinding>.converted(item: PersonEntity.RecentCooperate) {
        binding.ivAvatar.loadImageAnimate(item.avatar)
        binding.tvName.text = item.name
        binding.tvTimes.text = String.format("x%d", item.times)
        binding.tvJobs.text = item.jobs.joinToString("、")
    }
}