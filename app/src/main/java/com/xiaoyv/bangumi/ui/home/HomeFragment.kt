package com.xiaoyv.bangumi.ui.home

import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentHomeBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.widget.kts.useNotNull

/**
 * Class: [HomeFragment]
 *
 * @author why
 * @since 11/24/23
 */
class HomeFragment : BaseViewModelFragment<FragmentHomeBinding, HomeViewModel>() {

    private val contentAdapter by lazy { HomeAdapter() }

    override fun initView() {
        binding.rvContent.setHasFixedSize(true)
        binding.rvContent.adapter = contentAdapter
    }

    override fun initData() {

    }

    override fun initListener() {
        contentAdapter.addOnItemChildClickListener(R.id.tv_today_title) { _, _, _ ->
            RouteHelper.jumpCalendar()
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.onHomeIndexLiveData.observe(this) {
            val contentItems: MutableList<Any> = it?.images.orEmpty().toMutableList()
            useNotNull(it?.calendar) {
                contentItems.add(0, this)
            }
            useNotNull(it?.banner) {
                contentItems.add(0, this)
            }

            binding.rvContent.setItemViewCacheSize(contentItems.size)
            contentAdapter.submitList(contentItems)
        }
    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}