package com.xiaoyv.bangumi.ui.profile.page.save

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.QuickAdapterHelper
import com.chad.library.adapter.base.loadState.LoadState
import com.chad.library.adapter.base.loadState.trailing.TrailingLoadStateAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentSaveListBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.config.GlobalConfig
import com.xiaoyv.common.config.annotation.CollectType
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.kts.getAttrColor

/**
 * Class: [SaveListFragment]
 *
 * @author why
 * @since 11/24/23
 */
class SaveListFragment : BaseViewModelFragment<FragmentSaveListBinding, SaveListViewModel>() {

    private val contentAdapter by lazy { SaveListAdapter() }

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

    private val mediaTypes get() = GlobalConfig.mediaTypes

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.userId = arguments.getString(NavKey.KEY_STRING).orEmpty()
    }

    override fun initView() {
        binding.srlRefresh.initRefresh { viewModel.isRefresh }
        binding.srlRefresh.setColorSchemeColors(hostActivity.getAttrColor(GoogleAttr.colorPrimary))
    }

    override fun initData() {
        binding.rvContent.setHasFixedSize(true)
        binding.rvContent.adapter = adapterHelper.adapter

        viewModel.refresh()
    }

    override fun initListener() {
        binding.listType.setOnCheckedStateChangeListener { chipGroup, ints ->
            val type = when (ints.firstOrNull()) {
                R.id.type_wish -> CollectType.TYPE_WISH
                R.id.type_collect -> CollectType.TYPE_COLLECT
                R.id.type_do -> CollectType.TYPE_DO
                R.id.type_on_hold -> CollectType.TYPE_ON_HOLD
                R.id.type_dropped -> CollectType.TYPE_DROPPED
                else -> null
            }

            if (type != null) {
                viewModel.listType = type
                viewModel.refresh()
            }
        }

        binding.chipMediaType.setOnFastLimitClickListener {
            val item = mediaTypes.map { it.title }.toTypedArray()
            MaterialAlertDialogBuilder(hostActivity)
                .setItems(item) { _, position ->
                    binding.chipMediaType.text = mediaTypes[position].title
                    viewModel.mediaType = mediaTypes[position].type
                    viewModel.refresh()
                }
                .create()
                .show()
        }

        binding.srlRefresh.setOnRefreshListener {
            adapterHelper.trailingLoadState = LoadState.None
            viewModel.refresh()
        }

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_save) {
            RouteHelper.jumpMediaDetail(it.subjectId)
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.onListLiveData.observe(this) {
            debugLog { it.toJson(true) }

            contentAdapter.submitList(it.orEmpty()) {
                if (viewModel.isRefresh) {
                    layoutManager?.scrollToPositionWithOffset(0, 0)
                }

                adapterHelper.trailingLoadState = viewModel.loadingMoreState
            }
        }

        UserHelper.observe(this) {
            if (it.isEmpty) {
                viewModel.clearList()
            } else {
                viewModel.userId = it.id.orEmpty()
                viewModel.refresh()
            }
        }
    }

    companion object {
        fun newInstance(userId: String): SaveListFragment {
            return SaveListFragment().apply {
                arguments = bundleOf(NavKey.KEY_STRING to userId)
            }
        }
    }
}