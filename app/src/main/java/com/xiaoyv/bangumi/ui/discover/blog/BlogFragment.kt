package com.xiaoyv.bangumi.ui.discover.blog

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.QuickAdapterHelper
import com.chad.library.adapter.base.loadState.LoadState
import com.chad.library.adapter.base.loadState.trailing.TrailingLoadStateAdapter
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentBlogBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.kts.getAttrColor

/**
 * Class: [BlogFragment]
 *
 * @author why
 * @since 11/24/23
 */
class BlogFragment : BaseViewModelFragment<FragmentBlogBinding, BlogViewModel>() {
    private val contentAdapter by lazy { BlogAdapter() }

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

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.userId = arguments.getString(NavKey.KEY_STRING).orEmpty()
        viewModel.requireLogin = arguments.getBoolean(NavKey.KEY_BOOLEAN, false)
    }

    override fun initView() {
        binding.srlRefresh.initRefresh { false }
        binding.srlRefresh.setColorSchemeColors(hostActivity.getAttrColor(GoogleAttr.colorPrimary))

        // 用户页面嵌套时，不显示分类
        binding.gpFilter.isGone = viewModel.userId.isNotBlank()
    }

    override fun initData() {
        binding.rvContent.adapter = adapterHelper.adapter
    }

    override fun initListener() {
        binding.listType.setOnCheckedStateChangeListener { _, ints ->
            val type = when (ints.firstOrNull()) {
                R.id.type_anime -> MediaType.TYPE_ANIME
                R.id.type_book -> MediaType.TYPE_BOOK
                R.id.type_music -> MediaType.TYPE_MUSIC
                R.id.type_game -> MediaType.TYPE_GAME
                R.id.type_real -> MediaType.TYPE_REAL
                else -> null
            }

            if (type != null) {
                viewModel.mediaType = type
                viewModel.refresh()
            }
        }

        binding.typeTag.setOnFastLimitClickListener {
        }

        binding.srlRefresh.setOnRefreshListener {
            adapterHelper.trailingLoadState = LoadState.None
            viewModel.refresh()
        }

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_root) {
            RouteHelper.jumpBlogDetail(it.id)
        }

        // 嵌套在 Profile 页面的情况
        if (viewModel.requireLogin) {
            UserHelper.observeUserInfo(this) {
                viewModel.userId = it.id.orEmpty()
                viewModel.refresh()
            }
        } else {
            viewModel.refresh()
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        binding.stateView.initObserver(
            lifecycleOwner = this,
            loadingBias = if (viewModel.requireLogin) 0.3f else 0.5f,
            loadingViewState = viewModel.loadingViewState,
            canShowLoading = { viewModel.isRefresh && !binding.srlRefresh.isRefreshing },
            canShowTip = { viewModel.isRefresh }
        )

        viewModel.onListLiveData.observe(this) {
            // 加载失败
            if (it == null) {
                // 刷新失败清空
                if (viewModel.isRefresh) {
                    contentAdapter.submitList(emptyList())
                }
                adapterHelper.trailingLoadState = viewModel.loadingMoreState
                return@observe
            }

            contentAdapter.submitList(it) {
                if (viewModel.isRefresh) {
                    layoutManager?.scrollToPositionWithOffset(0, 0)
                }

                adapterHelper.trailingLoadState = viewModel.loadingMoreState
            }
        }

        // 自己的内容删除时刷新列表
        if (viewModel.isMine) {
            UserHelper.observeAction(this) {
                if (it == BgmPathType.TYPE_BLOG) {
                    viewModel.refresh()
                }
            }
        }
    }

    companion object {
        fun newInstance(userId: String? = null, requireLogin: Boolean = false): BlogFragment {
            return BlogFragment().apply {
                arguments = bundleOf(
                    NavKey.KEY_STRING to userId,
                    NavKey.KEY_BOOLEAN to requireLogin
                )
            }
        }
    }
}