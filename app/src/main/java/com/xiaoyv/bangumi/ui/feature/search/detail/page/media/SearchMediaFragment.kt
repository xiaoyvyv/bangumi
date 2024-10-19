package com.xiaoyv.bangumi.ui.feature.search.detail.page.media

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.BaseDifferAdapter
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListFragment
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.bangumi.ui.feature.search.detail.SearchDetailActivity
import com.xiaoyv.bangumi.ui.feature.search.detail.SearchDetailViewModel
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.SearchResultEntity
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.SearchCatType
import com.xiaoyv.common.databinding.ViewSearchMediaFilterBinding
import com.xiaoyv.common.kts.CommonId
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.i18n
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.kts.showOptionsDialog
import com.xiaoyv.widget.callback.setOnFastLimitClickListener

/**
 * Class: [SearchMediaFragment]
 *
 * @author why
 * @since 1/18/24
 */
class SearchMediaFragment : BaseListFragment<SearchResultEntity, SearchMediaViewModel>() {
    private val activityViewModel by activityViewModels<SearchDetailViewModel>()
    private lateinit var filterBinding: ViewSearchMediaFilterBinding

    override val isOnlyOnePage: Boolean
        get() = false

    private val inputSearchItem
        get() = (activity as? SearchDetailActivity)?.viewModel?.currentSearchItem?.value

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.isSearchMedia = arguments.getBoolean(NavKey.KEY_BOOLEAN)
    }

    override fun onCreateContentAdapter(): BaseDifferAdapter<SearchResultEntity, *> {
        return SearchMediaAdapter()
    }

    override fun injectFilter(container: FrameLayout) {
        // 过滤菜单
        filterBinding = ViewSearchMediaFilterBinding.inflate(layoutInflater, container, true)
        filterBinding.root.doOnPreDraw {
            binding.rvContent.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = filterBinding.root.height
            }
        }

        viewModel.searchItems.forEachIndexed { index, searchItem ->
            when (index) {
                0 -> filterBinding.type0.text = searchItem.label
                1 -> filterBinding.type1.text = searchItem.label
                2 -> filterBinding.type2.text = searchItem.label
                3 -> filterBinding.type3.text = searchItem.label
                4 -> filterBinding.type4.text = searchItem.label
            }
        }

        // 人物搜索隐藏多余的
        if (viewModel.isSearchMedia.not()) {
            filterBinding.type2.isVisible = false
            filterBinding.type3.isVisible = false
            filterBinding.type4.isVisible = false
        }

        // 切换
        filterBinding.listType.setOnCheckedStateChangeListener { _, ints ->
            val type = when (ints.firstOrNull()) {
                CommonId.type_0 -> viewModel.itemIndex.value = 0
                CommonId.type_1 -> viewModel.itemIndex.value = 1
                CommonId.type_2 -> viewModel.itemIndex.value = 2
                CommonId.type_3 -> viewModel.itemIndex.value = 3
                CommonId.type_4 -> viewModel.itemIndex.value = 4
                else -> null
            }

            // 刷新类型
            if (type != null) {
                viewModel.refresh()
            }
        }

        // 类别切换
        filterBinding.typeMode.setOnFastLimitClickListener {
            requireActivity().showOptionsDialog(
                title = i18n(CommonString.search_match_mode),
                items = listOf(
                    i18n(CommonString.search_match_m),
                    i18n(CommonString.search_match_e)
                ),
                onItemClick = { _, position ->
                    viewModel.isLegacy.value = position == 1
                    viewModel.refresh()
                }
            )
        }

        // 选择搜索时选择的具体小类别
        initSearchSubtype()
    }


    override fun initListener() {
        super.initListener()

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_search) {
            if (viewModel.isSearchMedia) {
                RouteHelper.jumpMediaDetail(it.id)
            } else {
                RouteHelper.jumpPerson(it.id, it.isVirtual)
            }
        }
    }

    override fun autoInitData() {
        // 不自动加载
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

        viewModel.isLegacy.observe(this) {
            if (it == true) {
                filterBinding.typeMode.text = i18n(CommonString.search_match_e)
            } else {
                filterBinding.typeMode.text = i18n(CommonString.search_match_m)
            }
        }

        viewModel.itemIndex.observe(this) {
            when (it) {
                0 -> filterBinding.listType.check(CommonId.type_0)
                1 -> filterBinding.listType.check(CommonId.type_1)
                2 -> filterBinding.listType.check(CommonId.type_2)
                3 -> filterBinding.listType.check(CommonId.type_3)
                4 -> filterBinding.listType.check(CommonId.type_4)
            }
        }
    }

    /**
     * 初始化选择搜索时选择的具体小类别
     */
    private fun initSearchSubtype() {
        inputSearchItem?.let {
            when (it.pathType) {
                // 搜索条目
                BgmPathType.TYPE_SEARCH_SUBJECT -> {
                    when (it.id) {
                        SearchCatType.TYPE_ANIME -> filterBinding.listType.check(CommonId.type_0)
                        SearchCatType.TYPE_BOOK -> filterBinding.listType.check(CommonId.type_1)
                        SearchCatType.TYPE_MUSIC -> filterBinding.listType.check(CommonId.type_2)
                        SearchCatType.TYPE_GAME -> filterBinding.listType.check(CommonId.type_3)
                        SearchCatType.TYPE_REAL -> filterBinding.listType.check(CommonId.type_4)
                    }
                }
                // 搜索人物
                BgmPathType.TYPE_SEARCH_MONO -> {
                    when (it.id) {
                        SearchCatType.TYPE_CHARACTER -> filterBinding.listType.check(CommonId.type_0)
                        SearchCatType.TYPE_PERSON -> filterBinding.listType.check(CommonId.type_1)
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance(mediaOrMono: Boolean): Fragment {
            return SearchMediaFragment().apply {
                arguments = bundleOf(NavKey.KEY_BOOLEAN to mediaOrMono)
            }
        }
    }
}