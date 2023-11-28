package com.xiaoyv.bangumi.ui.discover.group

import com.xiaoyv.bangumi.databinding.FragmentGroupBinding
import com.xiaoyv.bangumi.ui.rakuen.RakuenFragment
import com.xiaoyv.bangumi.ui.rakuen.RakuenViewModel
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment

/**
 * Class: [GroupFragment]
 *
 * @author why
 * @since 11/24/23
 */
class GroupFragment : BaseViewModelFragment<FragmentGroupBinding, RakuenViewModel>() {
    override fun initView() {

    }

    override fun initData() {

    }

    companion object {
        fun newInstance(): GroupFragment {
            return GroupFragment()
        }
    }
}