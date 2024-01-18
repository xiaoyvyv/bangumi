package com.xiaoyv.bangumi.ui.feature.search.detail.page.index

import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.BaseDifferAdapter
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListFragment
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.bangumi.ui.feature.search.detail.SearchDetailViewModel
import com.xiaoyv.bangumi.ui.feature.search.detail.page.searchApiFilter
import com.xiaoyv.common.api.parser.entity.SearchResultEntity
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener

/**
 * Class: [SearchIndexFragment]
 *
 * @author why
 * @since 1/18/24
 */
class SearchIndexFragment : BaseListFragment<SearchResultEntity, SearchIndexViewModel>() {
    private val activityViewModel by activityViewModels<SearchDetailViewModel>()

    override val isOnlyOnePage: Boolean
        get() = false

    override fun onCreateContentAdapter(): BaseDifferAdapter<SearchResultEntity, *> {
        return SearchIndexAdapter { viewModel.keywords }
    }

    override fun injectFilter(container: FrameLayout) {
        searchApiFilter(
            activity = requireActivity(),
            layoutInflater = layoutInflater,
            container = container,
            recyclerView = binding.rvContent,
            onChangeOrder = {
                when (it) {
                    0 -> viewModel.order = null
                    1 -> viewModel.order = "id"
                }
            },
            onChangeMode = {
                viewModel.isLegacy.value = it
            },
            onRefresh = {
                viewModel.refresh()
            }
        )
    }

    override fun initListener() {
        super.initListener()

        // 目录搜索
        contentAdapter.setOnDebouncedChildClickListener(R.id.item_index) { entity ->
            RouteHelper.jumpIndexDetail(entity.id)
        }
    }

    override fun LifecycleOwner.initViewObserverExt() {
        activityViewModel.onKeyword.observe(this) {
            viewModel.keyword = it
            viewModel.refresh()
        }

        activityViewModel.onKeywordChange.observe(this) {
            // 清空内容
            if (it.isBlank()) {
                viewModel.keyword = ""
                viewModel.clearList()
                viewModel.refresh()
            }
        }
    }

    companion object {
        fun newInstance(): Fragment {
            return SearchIndexFragment()
        }
    }
}