package com.xiaoyv.bangumi.ui.feature.user.sign

import android.annotation.SuppressLint
import android.view.MotionEvent
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.databinding.FragmentUserSignBinding
import com.xiaoyv.bangumi.ui.feature.user.UserViewModel
import com.xiaoyv.blueprint.base.binding.BaseBindingFragment
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.widget.web.page.SignView

/**
 * Class: [SignFragment]
 *
 * @author why
 * @since 12/3/23
 */
class SignFragment : BaseBindingFragment<FragmentUserSignBinding>() {
    private val activityViewModel: UserViewModel by activityViewModels()
    private val signView by lazy {
        SignView(binding.webView)
    }

    override fun initView() {
        binding.stateView.showLoading(0.2f)
    }

    override fun initData() {
        signView.startLoad()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initListener() {
        binding.webView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.clRoot.requestDisallowInterceptTouchEvent(true)
                }

                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    binding.clRoot.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }

    }

    override fun LifecycleOwner.initViewObserver() {
        activityViewModel.onUserInfoLiveData.observe(this) {
            launchUI {
                signView.waitMounted()
                signView.setSign(it?.sign.orEmpty())
                binding.stateView.showContent()
            }
        }
    }

    companion object {
        fun newInstance(): SignFragment {
            return SignFragment()
        }
    }
}