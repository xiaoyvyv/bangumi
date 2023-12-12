package com.xiaoyv.bangumi.ui.discover.index

import androidx.core.view.updatePadding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.layoutmanager.QuickGridLayoutManager
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentListBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.common.api.parser.entity.IndexEntity
import com.xiaoyv.common.api.parser.entity.IndexItemEntity
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.forceCast
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.kts.dpi
import com.xiaoyv.widget.kts.getAttrColor

/**
 * Class: [IndexFragment]
 *
 * @author why
 * @since 11/24/23
 */
class IndexFragment : BaseViewModelFragment<FragmentListBinding, IndexViewModel>() {

    private val listAdapter by lazy { IndexAdapter() }

    override fun initView() {
        binding.srlRefresh.initRefresh { false }
        binding.srlRefresh.setColorSchemeColors(hostActivity.getAttrColor(GoogleAttr.colorPrimary))

        binding.rvContent.updatePadding(top = 8.dpi)
    }

    override fun initData() {
        binding.rvContent.layoutManager =
            QuickGridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)

        binding.rvContent.adapter = listAdapter

        viewModel.queryIndexHome()
    }

    override fun initListener() {
        binding.srlRefresh.setOnRefreshListener {
            viewModel.queryIndexHome()
        }

        listAdapter.setOnDebouncedChildClickListener(R.id.item_index_grid) {
            RouteHelper.jumpIndexDetail(it.entity.forceCast<IndexEntity.Grid>().id)
        }

        listAdapter.setOnDebouncedChildClickListener(R.id.item_index) {
            RouteHelper.jumpIndexDetail(it.entity.forceCast<IndexItemEntity>().id)
        }

        listAdapter.setOnDebouncedChildClickListener(com.xiaoyv.common.R.id.tv_more) {
            val isNewCreateIndex = it.entity.forceCast<Pair<String, Boolean>>().second
            when (it.type) {
                IndexAdapter.TYPE_TITLE -> {
                    RouteHelper.jumpIndexList(isNewCreateIndex)
                }
            }
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
        fun newInstance(): IndexFragment {
            return IndexFragment()
        }
    }
}