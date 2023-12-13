package com.xiaoyv.bangumi.ui.profile.page.group

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.layoutmanager.QuickGridLayoutManager
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListFragment
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.bangumi.ui.discover.group.detail.GroupDetailAdapter
import com.xiaoyv.bangumi.ui.discover.group.list.GroupListViewModel
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.config.bean.SampleAvatar
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.dpi

/**
 * Class: [UserGroupFragment]
 *
 * @author why
 * @since 12/14/23
 */
class UserGroupFragment : BaseListFragment<SampleAvatar, GroupListViewModel>() {

    override val isOnlyOnePage: Boolean
        get() = true

    override val loadingBias: Float
        get() = 0.3f

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.isSortByNewest = arguments.getBoolean(NavKey.KEY_BOOLEAN, false)
        viewModel.userId = arguments.getString(NavKey.KEY_STRING).orEmpty()
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

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<SampleAvatar, *> {
        return GroupDetailAdapter()
    }

    override fun onListDataFinish(list: List<SampleAvatar>) {

    }

    companion object {
        fun newInstance(userId: String): UserGroupFragment {
            return UserGroupFragment().apply {
                arguments = bundleOf(NavKey.KEY_STRING to userId)
            }
        }
    }
}