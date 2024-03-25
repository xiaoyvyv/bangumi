package com.xiaoyv.bangumi.special.thunder.page

import android.os.Bundle
import androidx.core.os.bundleOf
import com.chad.library.adapter.base.BaseDifferAdapter
import com.xiaoyv.bangumi.base.BaseListFragment
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.config.bean.TaskInfoEntity
import com.xiaoyv.common.config.bean.tab.ThunderTab
import com.xiaoyv.widget.kts.getParcelObj

/**
 * Class: [ThunderTaskFragment]
 *
 * @author why
 * @since 3/23/24
 */
class ThunderTaskFragment : BaseListFragment<TaskInfoEntity, ThunderTaskViewModel>() {
    override val isOnlyOnePage: Boolean
        get() = true

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.tabType = arguments.getParcelObj<ThunderTab>(NavKey.KEY_PARCELABLE).let {
            it?.type ?: viewModel.tabType
        }
    }

    override fun onCreateContentAdapter(): BaseDifferAdapter<TaskInfoEntity, *> {
        return ThunderTaskAdapter()
    }

    companion object {
        fun newInstance(tab: ThunderTab): ThunderTaskFragment {
            return ThunderTaskFragment().apply {
                arguments = bundleOf(NavKey.KEY_PARCELABLE to tab)
            }
        }
    }
}