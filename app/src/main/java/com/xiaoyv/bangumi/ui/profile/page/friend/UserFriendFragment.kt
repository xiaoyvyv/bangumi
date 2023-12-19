package com.xiaoyv.bangumi.ui.profile.page.friend

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListFragment
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.FriendEntity
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [UserFriendFragment]
 *
 * @author why
 * @since 12/14/23
 */
class UserFriendFragment : BaseListFragment<FriendEntity, UserFriendViewModel>() {
    override val isOnlyOnePage: Boolean
        get() = true

    override val loadingBias: Float
        get() = 0.3f

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.userId = arguments.getString(NavKey.KEY_STRING).orEmpty()
        viewModel.requireLogin = arguments.getBoolean(NavKey.KEY_BOOLEAN, false)
    }

    override fun initListener() {
        super.initListener()
        contentAdapter.setOnDebouncedChildClickListener(R.id.item_friend) {
            RouteHelper.jumpUserDetail(it.id)
        }
    }

    override fun LifecycleOwner.initViewObserverExt() {
        UserHelper.observeAction(this) {
            if (it == BgmPathType.TYPE_USER) {
                viewModel.refresh()
            }
        }
    }

    override fun autoInitData() {
        // 嵌套在 Profile 页面的情况
        if (viewModel.requireLogin) {
            UserHelper.observeUserInfo(viewLifecycleOwner) {
                viewModel.userId = it.id.orEmpty()
                viewModel.refresh()
            }
        } else {
            super.autoInitData()
        }
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<FriendEntity, *> {
        return UserFriendAdapter()
    }

    companion object {
        fun newInstance(userId: String, requireLogin: Boolean): UserFriendFragment {
            return UserFriendFragment().apply {
                arguments = bundleOf(
                    NavKey.KEY_STRING to userId,
                    NavKey.KEY_BOOLEAN to requireLogin
                )
            }
        }
    }
}