package com.xiaoyv.bangumi.ui.discover.home

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentHomeBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.config.annotation.HomeFeatureType
import com.xiaoyv.common.widget.scroll.AnimeLinearLayoutManager
import com.xiaoyv.widget.kts.useNotNull

/**
 * Class: [HomeFragment]
 *
 * @author why
 * @since 11/24/23
 */
class HomeFragment : BaseViewModelFragment<FragmentHomeBinding, HomeViewModel>() {

    private val contentAdapter by lazy {
        HomeAdapter(
            onClickFeature = {
                when (it.id) {
                    HomeFeatureType.TYPE_SEARCH -> RouteHelper.jumpSearch()
                    HomeFeatureType.TYPE_EMAIL -> RouteHelper.jumpMessage()
                    HomeFeatureType.TYPE_MAGI -> {

                    }

                    HomeFeatureType.TYPE_DOLLARS -> {

                    }

                    HomeFeatureType.TYPE_ALMANAC -> {
                        RouteHelper.jumpWeb(BgmApiManager.URL_ALMANAC)
                    }
                }
            },
            onClickMedia = {
                RouteHelper.jumpMediaDetail(it.id)
            }
        )
    }

    override fun initView() {
        binding.rvContent.layoutManager =
            AnimeLinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false).apply {
                extraLayoutSpaceScale = 3f
            }
    }

    override fun initData() {
        binding.rvContent.adapter = contentAdapter
    }

    override fun initListener() {
        contentAdapter.addOnItemChildClickListener(R.id.tv_today_title) { _, _, _ ->
            RouteHelper.jumpCalendar()
        }
        contentAdapter.addOnItemChildClickListener(R.id.tv_tomorrow_title) { _, _, _ ->
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

            contentAdapter.submitList(contentItems)
        }
    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}