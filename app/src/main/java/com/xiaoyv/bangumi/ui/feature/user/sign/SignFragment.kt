package com.xiaoyv.bangumi.ui.feature.user.sign

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.databinding.FragmentUserSignBinding
import com.xiaoyv.bangumi.ui.feature.user.UserViewModel
import com.xiaoyv.blueprint.base.binding.BaseBindingFragment
import com.xiaoyv.common.api.parser.parseHtml

/**
 * Class: [SignFragment]
 *
 * @author why
 * @since 12/3/23
 */
class SignFragment : BaseBindingFragment<FragmentUserSignBinding>() {
    private val activityViewModel: UserViewModel by activityViewModels()

    override fun initView() {
        binding.stateView.showLoading(0.2f)
    }

    override fun initData() {

    }

    override fun LifecycleOwner.initViewObserver() {
        activityViewModel.onUserInfoLiveData.observe(this) {
            val summary = it?.sign.orEmpty().trim()
            if (summary.isBlank()) {
                binding.stateView.showTip(message = "这个人很懒，还没有介绍内容呢")
                return@observe
            }

            binding.tvSummary.text = summary.parseHtml()
            binding.stateView.showContent()
        }
    }

    companion object {
        fun newInstance(): SignFragment {
            return SignFragment()
        }
    }
}