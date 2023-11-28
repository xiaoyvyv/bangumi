package com.xiaoyv.bangumi.ui.discover.index

import com.xiaoyv.bangumi.databinding.FragmentIndexBinding
import com.xiaoyv.bangumi.ui.rakuen.RakuenViewModel
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment

/**
 * Class: [IndexFragment]
 *
 * @author why
 * @since 11/24/23
 */
class IndexFragment : BaseViewModelFragment<FragmentIndexBinding, RakuenViewModel>() {
    override fun initView() {

    }

    override fun initData() {

    }

    companion object {
        fun newInstance(): IndexFragment {
            return IndexFragment()
        }
    }
}