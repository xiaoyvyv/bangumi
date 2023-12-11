package com.xiaoyv.bangumi.ui.feature.magi.rank

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentListBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.common.api.parser.entity.MagiRankEntity
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.forceCast
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.widget.scroll.AnimeLinearLayoutManager
import com.xiaoyv.widget.kts.getAttrColor

/**
 * Class: [MagiRankFragment]
 *
 * @author why
 * @since 11/24/23
 */
class MagiRankFragment : BaseViewModelFragment<FragmentListBinding, MagiRankViewModel>() {
    private val listAdapter by lazy { MagiRankAdapter() }


    override fun initView() {
        binding.srlRefresh.initRefresh { false }
        binding.srlRefresh.setColorSchemeColors(hostActivity.getAttrColor(GoogleAttr.colorPrimary))
    }

    override fun initData() {
        binding.rvContent.layoutManager =
            AnimeLinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false).apply {
                extraLayoutSpaceScale = 3f
            }

        binding.rvContent.adapter = listAdapter

        viewModel.querySyncRateRank()
    }

    override fun initListener() {
        binding.srlRefresh.setOnRefreshListener {
            viewModel.querySyncRateRank()
        }

        listAdapter.setOnDebouncedChildClickListener(R.id.item_rate) {
            RouteHelper.jumpUserDetail(it.entity.forceCast<MagiRankEntity.MagiRank>().id)
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        binding.stateView.initObserver(
            lifecycleOwner = this,
            loadingViewState = viewModel.loadingViewState,
            canShowLoading = { !binding.srlRefresh.isRefreshing }
        )

        viewModel.onItemLiveData.observe(this) {
            listAdapter.submitList(it.orEmpty())
        }
    }

    companion object {
        fun newInstance(): MagiRankFragment {
            return MagiRankFragment()
        }
    }
}