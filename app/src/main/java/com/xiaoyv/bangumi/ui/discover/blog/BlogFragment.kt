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
    }

    override fun initView() {
        binding.srlRefresh.initRefresh { false }
        binding.srlRefresh.setColorSchemeColors(hostActivity.getAttrColor(GoogleAttr.colorPrimary))

        // 用户页面嵌套时，不显示分类
        binding.gpFilter.isGone = viewModel.userId.isNotBlank()
    }

    override fun initData() {
        binding.rvContent.adapter = adapterHelper.adapter

        viewModel.refresh()
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
            /*      val item = mediaTypes.map { it.title }.toTypedArray()
                  MaterialAlertDialogBuilder(hostActivity)
                      .setItems(item) { _, position ->
      //                    binding.chipMediaType.text = mediaTypes[position].title
                          viewModel.mediaType = mediaTypes[position].type
                          viewModel.refresh()
                      }
                      .create()
                      .show()*/
        }

        binding.srlRefresh.setOnRefreshListener {
            adapterHelper.trailingLoadState = LoadState.None
            viewModel.refresh()
        }

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_root) {
            RouteHelper.jumpBlogDetail(it.id)
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        binding.stateView.initObserver(
            lifecycleOwner = this,
            loadingBias = if (viewModel.isMine) 0.3f else 0.5f,
            loadingViewState = viewModel.loadingViewState,
            canShowLoading = { viewModel.isRefresh && !binding.srlRefresh.isRefreshing },
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
            UserHelper.observeDeleteAction(this) {
                if (it == BgmPathType.TYPE_BLOG) {
                    viewModel.refresh()
                }
            }
        }
    }

    companion object {
        fun newInstance(userId: String? = null): BlogFragment {
            return BlogFragment().apply {
                arguments = bundleOf(NavKey.KEY_STRING to userId)
            }
        }
    }
}