package com.xiaoyv.bangumi.ui.discover.home

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentHomeBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.bangumi.ui.discover.DiscoverFragment
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.common.config.annotation.FeatureType
import com.xiaoyv.common.helper.callback.RecyclerItemTouchedListener
import com.xiaoyv.common.widget.scroll.AnimeLinearLayoutManager
import com.xiaoyv.widget.kts.toast
import com.xiaoyv.widget.kts.useNotNull

/**
 * Class: [HomeFragment]
 *
 * @author why
 * @since 11/24/23
 */
class HomeFragment : BaseViewModelFragment<FragmentHomeBinding, HomeViewModel>() {

    /**
     * 优化子条目嵌套横向滑动
     */
    private val touchedListener = RecyclerItemTouchedListener {
        val discoverFragment: DiscoverFragment =
            parentFragment as? DiscoverFragment ?: return@RecyclerItemTouchedListener
        discoverFragment.vp.isUserInputEnabled = it
    }

    private val contentAdapter by lazy {
        HomeAdapter(
            touchedListener = touchedListener,
            onClickFeature = {
                when (it.id) {
                    FeatureType.TYPE_SYNCER -> RouteHelper.jumpSyncer()
                    FeatureType.TYPE_EMAIL -> RouteHelper.jumpMessage()
                    FeatureType.TYPE_MAGI -> RouteHelper.jumpMagi()
                    FeatureType.TYPE_ANIME_YUC -> RouteHelper.jumpYuc()
                    FeatureType.TYPE_ALMANAC -> RouteHelper.jumpAlmanac()
                    FeatureType.TYPE_PROCESS -> RouteHelper.jumpFragmentPage(FeatureType.TYPE_PROCESS)
                    FeatureType.TYPE_RANK -> RouteHelper.jumpFragmentPage(FeatureType.TYPE_RANK)
                    FeatureType.TYPE_DETECT_ANIME -> RouteHelper.jumpDetectAnime()
                    FeatureType.TYPE_DETECT_CHARACTER -> RouteHelper.jumpDetectCharacter()
                    FeatureType.TYPE_MAGNET -> RouteHelper.jumpAnimeMagnet()
                    FeatureType.TYPE_DOLLARS -> RouteHelper.jumpDollars()
                    FeatureType.TYPE_WIKI -> {
                        toast("暂未开放")
//                        launchUI {
//                            withContext(Dispatchers.IO) {
//                                val response =
//                                    BgmApiManager.bgmJsonApi.queryDouBanUserInterest(
//                                        userId = "188360604"
//                                    )
//
//                                val string = response.body()?.string()
//                                debugLog { "XXX:" + string }
//                            }
//                        }
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
                extraLayoutSpaceScale = 7f
            }
    }

    override fun initData() {
        binding.rvContent.adapter = contentAdapter
    }

    override fun initListener() {
        contentAdapter.addOnItemChildClickListener(R.id.tv_today_title) { _, _, _ ->
            RouteHelper.jumpCalendar(true)
        }
        contentAdapter.addOnItemChildClickListener(R.id.tv_tomorrow_title) { _, _, _ ->
            RouteHelper.jumpCalendar(false)
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