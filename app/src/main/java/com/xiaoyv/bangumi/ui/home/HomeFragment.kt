package com.xiaoyv.bangumi.ui.home

import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.databinding.FragmentHomeBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment

/**
 * Class: [HomeFragment]
 *
 * @author why
 * @since 11/24/23
 */
class HomeFragment : BaseViewModelFragment<FragmentHomeBinding, HomeViewModel>() {

    private val contentAdapter by lazy { HomeAdapter() }

    override fun initView() {
        binding.rvContent.adapter = contentAdapter
    }

    override fun initData() {

    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.onHomeIndexLiveData.observe(this) {
            val cardEntities = it?.images.orEmpty()

            contentAdapter.submitList(cardEntities)
        }
    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}