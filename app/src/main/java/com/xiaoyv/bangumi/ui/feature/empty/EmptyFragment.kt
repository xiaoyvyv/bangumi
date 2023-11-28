package com.xiaoyv.bangumi.ui.feature.empty

import com.xiaoyv.bangumi.databinding.FragmentEmptyBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment

/**
 * Class: [EmptyFragment]
 *
 * @author why
 * @since 11/24/23
 */
class EmptyFragment : BaseViewModelFragment<FragmentEmptyBinding, EmptyViewModel>() {
    override fun initView() {

    }

    override fun initData() {

    }

    companion object {
        fun newInstance(): EmptyFragment {
            return EmptyFragment()
        }
    }
}