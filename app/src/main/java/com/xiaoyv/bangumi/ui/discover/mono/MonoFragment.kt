package com.xiaoyv.bangumi.ui.discover.mono

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.view.updatePadding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.layoutmanager.QuickGridLayoutManager
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentListBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.bean.SampleImageEntity
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.CommonId
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.kts.dpi
import com.xiaoyv.widget.kts.getAttrColor

/**
 * Class: [MonoFragment]
 *
 * @author why
 * @since 12/19/23
 */
class MonoFragment : BaseViewModelFragment<FragmentListBinding, MonoViewModel>() {
    private val contentAdapter by lazy {
        MonoAdapter().apply {
            ConfigHelper.configAdapterAnimation(this, binding.rvContent)
        }
    }

    /**
     * 宫格数目
     */
    private val spanCount: Int
        get() = if (viewModel.userId.isNotBlank() || viewModel.requireLogin) 4 else 3

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.userId = arguments.getString(NavKey.KEY_STRING).orEmpty()
        viewModel.requireLogin = arguments.getBoolean(NavKey.KEY_BOOLEAN, false)
    }

    override fun initView() {
        binding.srlRefresh.initRefresh { false }
        binding.srlRefresh.setColorSchemeColors(requireContext().getAttrColor(GoogleAttr.colorPrimary))

        binding.rvContent.updatePadding(left = 8.dpi, right = 8.dpi)
        binding.rvContent.layoutManager =
            QuickGridLayoutManager(requireContext(), spanCount, GridLayoutManager.VERTICAL, false)
    }

    override fun initData() {
        binding.rvContent.adapter = contentAdapter
    }

    override fun initListener() {
        binding.srlRefresh.setOnRefreshListener {
            viewModel.queryMono()
        }

        contentAdapter.setOnDebouncedChildClickListener(CommonId.tv_more) {
            val type = it.entity.toString()
            RouteHelper.jumpMonoList(
                isCharacter = type == BgmPathType.TYPE_CHARACTER,
                userId = viewModel.userId
            )
        }

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_mono) {
            val entity = it.entity as SampleImageEntity
            RouteHelper.jumpPerson(entity.id, entity.type == BgmPathType.TYPE_CHARACTER)
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        binding.stateView.initObserver(
            lifecycleOwner = this,
            loadingBias = if (viewModel.requireLogin) 0.3f else 0.5f,
            loadingViewState = viewModel.loadingViewState,
            canShowLoading = { !binding.srlRefresh.isRefreshing }
        )

        viewModel.onMonoIndexLiveData.observe(this) {
            val entity = it ?: return@observe
            contentAdapter.submitList(entity)
        }

        if (viewModel.requireLogin) {
            // 登录状态变化，刷新
            UserHelper.observeUserInfo(this) {
                viewModel.userId = it.id
                viewModel.queryMono()
            }

            // 收藏变化刷新
            UserHelper.observeAction(this) {
                if (it == BgmPathType.TYPE_CHARACTER || it == BgmPathType.TYPE_PERSON) {
                    viewModel.queryMono()
                }
            }
        } else {
            viewModel.queryMono()
        }

    }

    companion object {
        fun newInstance(userId: String? = null, requireLogin: Boolean = false): MonoFragment {
            return MonoFragment().apply {
                arguments = bundleOf(
                    NavKey.KEY_STRING to userId,
                    NavKey.KEY_BOOLEAN to requireLogin
                )
            }
        }
    }
}