package com.xiaoyv.bangumi.ui.feature.search.detail

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.KeyboardUtils
import com.chad.library.adapter.base.QuickAdapterHelper
import com.chad.library.adapter.base.loadState.trailing.TrailingLoadStateAdapter
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.ActivitySearchDetailBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.SearchCatType
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.kts.getAttrColor
import com.xiaoyv.widget.kts.getParcelObj
import com.xiaoyv.widget.kts.useNotNull

/**
 * Class: [SearchDetailActivity]
 *
 * @author why
 * @since 12/10/23
 */
class SearchDetailActivity :
    BaseViewModelActivity<ActivitySearchDetailBinding, SearchDetailViewModel>() {
    private val contentAdapter by lazy {
        SearchDetailAdapter()
    }

    private val adapterHelper by lazy {
        QuickAdapterHelper.Builder(contentAdapter)
            .setTrailingLoadStateAdapter(object : TrailingLoadStateAdapter.OnTrailingListener {
                override fun isAllowLoading(): Boolean {
                    return binding.srlRefresh.isRefreshing.not()
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

    private val layoutManager: LinearLayoutManager?
        get() = binding.rvContent.layoutManager as? LinearLayoutManager

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.currentSearchItem.value = bundle.getParcelObj(NavKey.KEY_PARCELABLE)
    }

    override fun initView() {
        binding.srlRefresh.initRefresh { false }
        binding.srlRefresh.setColorSchemeColors(getAttrColor(GoogleAttr.colorPrimary))

        binding.rvContent.adapter = adapterHelper.adapter
    }

    override fun initData() {
        viewModel.refresh()
    }

    override fun initListener() {
        binding.searchBar.tvCancel.setOnFastLimitClickListener {
            finish()
        }

        binding.srlRefresh.setOnRefreshListener {
            viewModel.refresh()
        }

        binding.searchBar.etKeyword.setOnEditorActionListener { _, _, _ ->
            val keyword = binding.searchBar.etKeyword.text.toString().trim()
            if (keyword.isNotBlank()) {
                viewModel.currentSearchItem.value?.keyword = keyword
                viewModel.refresh()

                KeyboardUtils.hideSoftInput(this)
            }
            true
        }

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_search) {
            useNotNull(viewModel.currentSearchItem.value) {
                if (pathType == BgmPathType.TYPE_SEARCH_SUBJECT) {
                    RouteHelper.jumpMediaDetail(it.id)
                } else {
                    RouteHelper.jumpPerson(it.id, id == SearchCatType.TYPE_CHARACTER)
                }
            }
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        binding.stateView.initObserver(
            lifecycleOwner = this,
            loadingViewState = viewModel.loadingViewState,
            canShowLoading = { viewModel.isRefresh && !binding.srlRefresh.isRefreshing }
        )

        viewModel.currentSearchItem.observe(this) {
            val searchItem = it ?: return@observe
            binding.searchBar.etKeyword.setText(searchItem.keyword)
            binding.searchBar.etKeyword.hint = buildString {
                append("搜索：")
                append(BgmPathType.string(it.pathType))
                append(" - ")
                append(it.label)
            }
        }


        viewModel.onListLiveData.observe(this) {
            if (it == null) {
                return@observe
            }

            contentAdapter.submitList(it) {
                if (viewModel.isRefresh) {
                    layoutManager?.scrollToPositionWithOffset(0, 0)
                }

                adapterHelper.trailingLoadState = viewModel.loadingMoreState
            }
        }
    }
}