package com.xiaoyv.bangumi.ui.feature.magi.question

import com.xiaoyv.bangumi.databinding.FragmentMagiQuestionBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment

/**
 * Class: [MagiQuestionFragment]
 *
 * @author why
 * @since 11/24/23
 */
class MagiQuestionFragment :
    BaseViewModelFragment<FragmentMagiQuestionBinding, MagiQuestionViewModel>() {

    override fun initView() {

    }

    override fun initData() {

    }
    companion object {
        fun newInstance(): MagiQuestionFragment {
            return MagiQuestionFragment()
        }
    }
}