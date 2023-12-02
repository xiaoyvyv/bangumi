package com.xiaoyv.bangumi.base

import androidx.annotation.CallSuper
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseDifferAdapter
import com.chad.library.adapter.base.QuickAdapterHelper
import com.chad.library.adapter.base.loadState.trailing.TrailingLoadStateAdapter
import com.xiaoyv.bangumi.databinding.FragmentListBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.getAttrColor

/**
 * Class: [BaseListFragment]
 *
 * @author why
 * @since 11/29/23
 */
abstract class BaseListFragment<T, VM : BaseListViewModel<T>> :
    BaseViewModelFragment<FragmentListBinding, VM>() {

    abstract val isOnlyOnePage: Boolean

    internal val contentAdapter: BaseDifferAdapter<T, *> by lazy {
        onCreateContentAdapter()
    }

    private val adapterHelper by lazy {
        QuickAdapterHelper.Builder(contentAdapter)
            .setTrailingLoadStateAdapter(object : TrailingLoadStateAdapter.OnTrailingListener {
                override fun isAllowLoading(): Boolean {
                    return binding.srlRefresh.isRefreshing.not() && isOnlyOnePage.not()
                }

                override fun onFailRetry() {
                    viewModel.loadMore()
                }

                override fun onLoad() {
                    viewModel.loadMore()
                }
            })
            .build()
    }

    internal open val hasFixedSize = false

    internal open val layoutManager: LinearLayoutManager?
        get() = binding.rvContent.layoutManager as? LinearLayoutManager

    abstract fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<T, *>

    @CallSuper
    override fun initView() {
        binding.rvContent.setHasFixedSize(hasFixedSize)
        if (isOnlyOnePage) {
            binding.rvContent.adapter = contentAdapter
        } else {
            binding.rvContent.adapter = adapterHelper.adapter
        }

        binding.srlRefresh.initRefresh { viewModel.isRefresh }
        binding.srlRefresh.setColorSchemeColors(hostActivity.getAttrColor(GoogleAttr.colorPrimary))
    }

    @CallSuper
    override fun initData() {
        viewModel.refresh()
    }

    @CallSuper
    override fun initListener() {
        binding.srlRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    @CallSuper
    override fun LifecycleOwner.initViewObserver() {
        viewModel.onListLiveData.observe(this) {
            debugLog { "List:\n " + it.toJson(true) }

            contentAdapter.submitList(it.orEmpty()) {
                if (viewModel.isRefresh) {
                    layoutManager?.scrollToPositionWithOffset(0, 0)
                }

                adapterHelper.trailingLoadState = viewModel.loadingMoreState
            }
        }
    }
}