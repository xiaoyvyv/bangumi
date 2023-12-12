package com.xiaoyv.bangumi.ui.discover.index.list

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListActivity
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.IndexItemEntity
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [GroupListActivity]
 *
 * @author why
 * @since 12/12/23
 */
class IndexListActivity : BaseListActivity<IndexItemEntity, IndexListViewModel>() {

    override val toolbarTitle: String
        get() = "目录列表"

    override val isOnlyOnePage: Boolean
        get() = false

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.isSortByNewest = bundle.getBoolean(NavKey.KEY_BOOLEAN, false)
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

    override fun onListDataFinish(list: List<IndexItemEntity>) {
        binding.toolbar.title = if (viewModel.isSortByNewest) {
            "目录列表-时间排序"
        } else {
            "目录列表-热门排序"
        }

        invalidateMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (viewModel.isSortByNewest) {
            menu.add("按热门排序")
                .setOnMenuItemClickListener {
                    viewModel.isSortByNewest = false
                    viewModel.refresh()
                    true
                }
        } else {
            menu.add("按创建时间排序")
                .setOnMenuItemClickListener {
                    viewModel.isSortByNewest = true
                    viewModel.refresh()
                    true
                }
        }
        return super.onCreateOptionsMenu(menu)
    }
}