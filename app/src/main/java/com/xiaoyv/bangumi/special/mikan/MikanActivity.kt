package com.xiaoyv.bangumi.special.mikan

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.StringUtils
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListActivity
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.MikanEntity
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.i18n
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.showToastCompat

/**
 * Class: [MikanActivity]
 *
 * @author why
 * @since 3/20/24
 */
class MikanActivity : BaseListActivity<MikanEntity.Group, MikanViewModel>() {
    override val toolbarTitle: String
        get() = StringUtils.getString(CommonString.mikan_title)

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.mikanId = bundle.getString(NavKey.KEY_STRING).orEmpty()
    }

    override val isOnlyOnePage: Boolean
        get() = true

    override fun initListener() {
        super.initListener()

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_group) {
            RouteHelper.jumpMikanGroupResource(it.mikanId, it.id, it.name)
        }

        contentAdapter.setOnDebouncedChildClickListener(R.id.iv_rss) {
            ClipboardUtils.copyText(it.rssUrl)

            showToastCompat("RSS 订阅链接复制成功！")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(i18n(CommonString.common_help))
            .setIcon(CommonDrawable.ic_help)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            .setOnMenuItemClickListener {
                showConfirmDialog(message = "数据来源：mikanani.me", cancelText = null)
                true
            }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<MikanEntity.Group, *> {
        return MikanAdapter()
    }
}