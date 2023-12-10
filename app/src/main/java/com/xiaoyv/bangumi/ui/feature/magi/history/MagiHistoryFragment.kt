package com.xiaoyv.bangumi.ui.feature.magi.history

import com.xiaoyv.bangumi.databinding.FragmentMagiHistoryBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment

/**
 * Class: [MagiHistoryFragment]
 *
 * @author why
 * @since 11/24/23
 */
class MagiHistoryFragment :
    BaseViewModelFragment<FragmentMagiHistoryBinding, MagiHistoryViewModel>() {

    override fun initData() {

    }

    override fun initView() {
    }

    companion object {
        fun newInstance(): MagiHistoryFragment {
            return MagiHistoryFragment()
        }
    }
}