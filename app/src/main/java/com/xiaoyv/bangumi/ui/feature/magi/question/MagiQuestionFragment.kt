package com.xiaoyv.bangumi.ui.feature.magi.question

import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.databinding.FragmentMagiQuestionBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.kts.debugLog

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
        viewModel.queryQuestion()
    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.onMagiQuestionLiveData.observe(this) {
            val entity = it ?: return@observe

            debugLog { entity.toJson(true) }
        }
    }

    companion object {
        fun newInstance(): MagiQuestionFragment {
            return MagiQuestionFragment()
        }
    }
}