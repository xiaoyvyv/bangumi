package com.xiaoyv.bangumi.ui.feature.search.detail

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.KeyboardUtils
import com.chad.library.adapter.base.QuickAdapterHelper
import com.chad.library.adapter.base.loadState.trailing.TrailingLoadStateAdapter
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.ActivitySearchDetailBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.bangumi.ui.feature.search.detail.adapter.SearchDetailItemAdapter
import com.xiaoyv.bangumi.ui.feature.search.detail.adapter.SearchDetailTagAdapter
import com.xiaoyv.bangumi.ui.feature.search.detail.adapter.SearchTopicAdapter
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.config.GlobalConfig
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.SearchCatType
import com.xiaoyv.common.config.annotation.TopicType
import com.xiaoyv.common.config.bean.PostAttach
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.widget.scroll.AnimeLinearLayoutManager
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

    /**
     * Adapter
     */
    private val contentItemAdapter by lazy { SearchDetailItemAdapter() }
    private val contentTagAdapter by lazy { SearchDetailTagAdapter() }
    private val contentTopicAdapter by lazy { SearchTopicAdapter() }

    /**
     * 适配器
     */
    private val contentAdapter
        get() = when (viewModel.searchBgmType) {
            BgmPathType.TYPE_SEARCH_TAG -> contentTagAdapter
            BgmPathType.TYPE_INDEX -> contentTagAdapter
            BgmPathType.TYPE_TOPIC -> contentTopicAdapter
            else -> contentItemAdapter
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

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.currentSearchItem.value = bundle.getParcelObj(NavKey.KEY_PARCELABLE)
    }

    override fun initView() {
        binding.srlRefresh.initRefresh { false }
        binding.srlRefresh.setColorSchemeColors(getAttrColor(GoogleAttr.colorPrimary))
    }

    override fun initData() {
        when (viewModel.searchBgmType) {
            // 搜索标签
            BgmPathType.TYPE_SEARCH_TAG -> {
                binding.rvContent.layoutManager = FlexboxLayoutManager(this, FlexDirection.ROW)
                binding.rvContent.adapter = contentAdapter
            }

            else -> {
                binding.rvContent.layoutManager =
                    AnimeLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                binding.rvContent.adapter = adapterHelper.adapter
            }
        }

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

        contentItemAdapter.setOnDebouncedChildClickListener(R.id.item_search) {
            useNotNull(viewModel.currentSearchItem.value) {
                // 选取模式
                if (viewModel.forSelectedMedia) {
                    setResult(
                        RESULT_OK,
                        Intent().putExtra(
                            NavKey.KEY_PARCELABLE, PostAttach(
                                id = it.id,
                                title = it.title,
                                image = it.coverImage,
                                type = GlobalConfig.mediaTypeName(it.searchMediaType)
                            )
                        )
                    )
                    finish()
                }
                // 正常搜索模式
                else {
                    if (pathType == BgmPathType.TYPE_SEARCH_SUBJECT) {
                        RouteHelper.jumpMediaDetail(it.id)
                    } else {
                        RouteHelper.jumpPerson(it.id, id == SearchCatType.TYPE_CHARACTER)
                    }
                }
            }
        }

        contentTagAdapter.setOnDebouncedChildClickListener(R.id.item_tag) { entity ->
            useNotNull(viewModel.currentSearchItem.value) {
                // 针对标签的搜索结果，SearchItem 的 id 在 SearchViewModel 填充为 MediaType
                val tagMediaType = this.id
                val tagName = entity.id
                RouteHelper.jumpTagDetail(tagMediaType, tagName)
            }
        }

        // 小组话题帖子搜索
        contentTopicAdapter.setOnDebouncedChildClickListener(R.id.item_collection) { entity ->
            RouteHelper.jumpTopicDetail(entity.id, TopicType.TYPE_GROUP)
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        binding.stateView.initObserver(
            lifecycleOwner = this,
            loadingViewState = viewModel.loadingViewState,
            canShowLoading = { viewModel.isRefresh && !binding.srlRefresh.isRefreshing },
            canShowTip = { viewModel.isRefresh }
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
                    scrollTop()
                }
                adapterHelper.trailingLoadState = viewModel.loadingMoreState
            }
        }
    }

    private fun scrollTop() {
        when (viewModel.searchBgmType) {
            // 搜索标签
            BgmPathType.TYPE_SEARCH_TAG -> useNotNull(binding.rvContent.layoutManager as? FlexboxLayoutManager) {
                scrollToPosition(0)
            }
            // 其它
            else -> useNotNull(binding.rvContent.layoutManager as? LinearLayoutManager) {
                scrollToPositionWithOffset(0, 0)
            }
        }
    }
}