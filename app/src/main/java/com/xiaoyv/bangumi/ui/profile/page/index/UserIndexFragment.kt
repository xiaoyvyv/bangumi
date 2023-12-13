package com.xiaoyv.bangumi.ui.profile.page.index

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.view.updatePadding
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListFragment
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.bangumi.ui.discover.index.list.IndexListAdapter
import com.xiaoyv.bangumi.ui.discover.index.list.IndexListViewModel
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.IndexItemEntity
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.dpi

/**
 * Class: [UserIndexFragment]
 *
 * @author why
 * @since 12/14/23
 */
class UserIndexFragment : BaseListFragment<IndexItemEntity, IndexListViewModel>() {

    override val isOnlyOnePage: Boolean
        get() = false

    override val loadingBias: Float
        get() = 0.3f

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.userId = arguments.getString(NavKey.KEY_STRING).orEmpty()
    }

    override fun initListener() {
        super.initListener()

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_index) {
            RouteHelper.jumpIndexDetail(it.id)
        }
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<IndexItemEntity, *> {
        return IndexListAdapter()
    }

    override fun LifecycleOwner.initViewObserverExt() {
        // 自己的内容删除时刷新列表
        if (viewModel.isMine) {
            UserHelper.observeDeleteAction(this) {
                if (it == BgmPathType.TYPE_INDEX) {
                    viewModel.refresh()
                }
            }
        }
    }

    override fun onListDataFinish(list: List<IndexItemEntity>) {

    }

    companion object {
        fun newInstance(userId: String): UserIndexFragment {
            return UserIndexFragment().apply {
                arguments = bundleOf(NavKey.KEY_STRING to userId)
            }
        }
    }
}