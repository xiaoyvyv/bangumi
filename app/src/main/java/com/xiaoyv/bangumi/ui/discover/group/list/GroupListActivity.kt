package com.xiaoyv.bangumi.ui.discover.group.list

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.layoutmanager.QuickGridLayoutManager
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListActivity
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.bangumi.ui.discover.group.detail.GroupDetailAdapter
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.config.bean.SampleImageEntity
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [GroupListActivity]
 *
 * @author why
 * @since 12/12/23
 */
class GroupListActivity : BaseListActivity<SampleImageEntity, GroupListViewModel>() {

    override val toolbarTitle: String
        get() = "小组列表"

    override val isOnlyOnePage: Boolean
        get() = false

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.isSortByNewest = bundle.getBoolean(NavKey.KEY_BOOLEAN, false)
    }

    override fun onCreateLayoutManager(): LinearLayoutManager {
        return QuickGridLayoutManager(this, 5, GridLayoutManager.VERTICAL, false)
    }

    override fun initListener() {
        super.initListener()

        contentAdapter.setOnDebouncedChildClickListener(R.id.iv_avatar) {
            RouteHelper.jumpGroupDetail(it.id)
        }
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<SampleImageEntity, *> {
        return GroupDetailAdapter()
    }

    override fun onListDataFinish(list: List<SampleImageEntity>) {
        binding.toolbar.title = buildString {
            append("小组列表-")
            append(viewModel.currentCategoryName())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        viewModel.categoryMap.forEachIndexed { index, pair ->
            val subMenu = menu
                .addSubMenu(0, index, index, pair.first)

            pair.second.forEach { sub ->
                subMenu.add(sub.second)
                    .setOnMenuItemClickListener {
                    viewModel.category = sub.first
                    viewModel.refresh()
                    true
                }
            }
        }

        return super.onCreateOptionsMenu(menu)
    }
}