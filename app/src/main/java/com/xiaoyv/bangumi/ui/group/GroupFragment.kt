package com.xiaoyv.bangumi.ui.group

import com.xiaoyv.bangumi.databinding.FragmentGroupBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment

/**
 * Class: [GroupFragment]
 *
 * @author why
 * @since 11/24/23
 */
class GroupFragment : BaseViewModelFragment<FragmentGroupBinding, GroupViewModel>() {
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