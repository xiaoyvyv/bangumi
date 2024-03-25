package com.xiaoyv.bangumi.special.thunder.page

import com.xiaoyv.bangumi.databinding.FragmentThunderTaskItemBinding
import com.xiaoyv.common.config.bean.TaskInfoEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [ThunderTaskAdapter]
 *
 * @author why
 * @since 3/20/24
 */
class ThunderTaskAdapter : BaseQuickDiffBindingAdapter<TaskInfoEntity,
        FragmentThunderTaskItemBinding>(IdDiffItemCallback()) {
    override fun BaseQuickBindingHolder<FragmentThunderTaskItemBinding>.converted(item: TaskInfoEntity) {
        binding.tvTitle.text = item.xlTaskInfo?.mFileName
    }
}