package com.xiaoyv.bangumi.special.yuc.detail

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListActivity
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.BrowserEntity
import com.xiaoyv.common.config.annotation.BrowserSortType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.helper.addCommonMenu
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [YucDetailActivity]
 *
 * @author why
 * @since 3/19/24
 */
class YucDetailActivity : BaseListActivity<BrowserEntity.Item, YucDetailViewModel>() {
    override val isOnlyOnePage: Boolean
        get() = false

    override val toolbarTitle: String
        get() = buildString {
            append(viewModel.yearMonth)
            append("月新番")
        }

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.yearMonth = bundle.getString(NavKey.KEY_STRING).orEmpty()
    }

    override fun initListener() {
        super.initListener()

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_media) {
            RouteHelper.jumpMediaDetail(it.id)
        }
    }

    override fun onListDataFinish(list: List<BrowserEntity.Item>) {
        invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(if (viewModel.sortType == BrowserSortType.TYPE_DATE) "按默认排序" else "按日期倒序")
            .setOnMenuItemClickListener {
                if (viewModel.sortType == BrowserSortType.TYPE_DATE) {
                    viewModel.sortType = BrowserSortType.TYPE_DEFAULT
                } else {
                    viewModel.sortType = BrowserSortType.TYPE_DATE
                }
                viewModel.refresh()
                true
            }

        menu.add("Yuc 详情")
            .setOnMenuItemClickListener {
                val yucUrl = viewModel.buildYucUrl()

                RouteHelper.jumpWeb(
                    url = yucUrl,
                    fitToolbar = true,
                    smallToolbar = true,
                    forceBrowser = false,
                    disableHandUrl = true
                )
                true
            }

        menu.addCommonMenu(BgmApiManager.buildMediaTvUrl(MediaType.TYPE_ANIME, viewModel.yearMonth))
        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<BrowserEntity.Item, *> {
        return YucDetailAdapter()
    }
}