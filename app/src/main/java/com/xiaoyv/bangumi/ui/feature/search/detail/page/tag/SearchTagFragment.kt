package com.xiaoyv.bangumi.ui.feature.search.detail.page.tag

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseDifferAdapter
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListFragment
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.bangumi.ui.feature.search.detail.SearchDetailActivity
import com.xiaoyv.bangumi.ui.feature.search.detail.SearchDetailViewModel
import com.xiaoyv.common.api.parser.entity.SearchResultEntity
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.kts.useNotNull

/**
 * Class: [SearchTagFragment]
 *
 * @author why
 * @since 1/18/24
 */
class SearchTagFragment : BaseListFragment<SearchResultEntity, SearchTagViewModel>() {
    private val activityViewModel by activityViewModels<SearchDetailViewModel>()

    private val inputSearchItem
        get() = (activity as? SearchDetailActivity)?.viewModel?.currentSearchItem?.value

    override val isOnlyOnePage: Boolean
        get() = true

    override fun onCreateContentAdapter(): BaseDifferAdapter<SearchResultEntity, *> {
        return SearchTagAdapter()
    }

    override fun onCreateLayoutManager(): RecyclerView.LayoutManager {
        return FlexboxLayoutManager(requireContext(), FlexDirection.ROW)
    }

    override fun initView() {
        super.initView()

        // TAG 搜索类型初始化
        inputSearchItem?.let {
            if (it.pathType == BgmPathType.TYPE_SEARCH_TAG) when (it.id) {
                MediaType.TYPE_ANIME -> viewModel.itemIndex.value = 0
                MediaType.TYPE_BOOK -> viewModel.itemIndex.value = 1
                MediaType.TYPE_MUSIC -> viewModel.itemIndex.value = 2
                MediaType.TYPE_GAME -> viewModel.itemIndex.value = 3
                MediaType.TYPE_REAL -> viewModel.itemIndex.value = 4
            }
        }
    }

    override fun initListener() {
        super.initListener()

        // 标签搜索
        contentAdapter.setOnDebouncedChildClickListener(R.id.item_tag) { entity ->
            useNotNull(viewModel.searchItem) {
                // 针对标签的搜索结果，SearchItem 的 id 在 SearchViewModel 填充为 MediaType
                val tagMediaType = this.id
                val tagName = entity.id
                RouteHelper.jumpTagDetail(tagMediaType, tagName)
            }
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
            return SearchTagFragment()
        }
    }
}