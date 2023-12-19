package com.xiaoyv.bangumi.ui.profile.page.group

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.view.updatePadding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.layoutmanager.QuickGridLayoutManager
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListFragment
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.bangumi.ui.discover.group.detail.GroupDetailAdapter
import com.xiaoyv.bangumi.ui.discover.group.list.GroupListViewModel
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.bean.SampleImageEntity
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.dpi

/**
 * Class: [UserGroupFragment]
 *
 * @author why
 * @since 12/14/23
 */
class UserGroupFragment : BaseListFragment<SampleImageEntity, GroupListViewModel>() {

    override val isOnlyOnePage: Boolean
        get() = true

    override val loadingBias: Float
        get() = 0.3f

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.isSortByNewest = arguments.getBoolean(NavKey.KEY_BOOLEAN, false)
        viewModel.userId = arguments.getString(NavKey.KEY_STRING).orEmpty()
        viewModel.requireLogin = arguments.getBoolean(NavKey.KEY_BOOLEAN, false)
    }

    override fun onCreateLayoutManager(): LinearLayoutManager {
        return QuickGridLayoutManager(requireContext(), 5, GridLayoutManager.VERTICAL, false)
    }

    override fun initView() {
        super.initView()
        binding.rvContent.updatePadding(8.dpi, 8.dpi, 8.dpi, 8.dpi)
    }

    override fun initListener() {
        super.initListener()

        contentAdapter.setOnDebouncedChildClickListener(R.id.iv_avatar) {
            RouteHelper.jumpGroupDetail(it.id)
        }
    }

    override fun LifecycleOwner.initViewObserverExt() {
        // 小组变化时刷新
        if (viewModel.isMine) {
            UserHelper.observeAction(this) {
                if (it == BgmPathType.TYPE_GROUP) {
                    viewModel.refresh()
                }
            }
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

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<SampleImageEntity, *> {
        return GroupDetailAdapter()
    }

    override fun onListDataFinish(list: List<SampleImageEntity>) {

    }

    companion object {
        fun newInstance(userId: String, requireLogin: Boolean): UserGroupFragment {
            return UserGroupFragment().apply {
                arguments = bundleOf(
                    NavKey.KEY_STRING to userId,
                    NavKey.KEY_BOOLEAN to requireLogin
                )
            }
        }
    }
}