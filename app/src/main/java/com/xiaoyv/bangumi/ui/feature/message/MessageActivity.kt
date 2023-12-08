package com.xiaoyv.bangumi.ui.feature.message

import android.view.Menu
import android.view.MenuItem
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListActivity
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.common.api.parser.entity.MessageEntity
import com.xiaoyv.common.config.annotation.MessageBoxType
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [MessageActivity]
 *
 * @author why
 * @since 12/8/23
 */
class MessageActivity : BaseListActivity<MessageEntity, MessageViewModel>() {
    override val isOnlyOnePage: Boolean
        get() = false

    override val toolbarTitle: String
        get() = "短信"

    override fun initListener() {
        super.initListener()

        contentAdapter.setOnDebouncedChildClickListener(R.id.iv_avatar) {
            RouteHelper.jumpUserDetail(it.fromId)
        }

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_notify) {
            RouteHelper.jumpMessageDetail(it.id, it.fromName)
        }
    }

    override fun onBindListDataFinish(list: List<MessageEntity>) {
        if (viewModel.boxType == MessageBoxType.TYPE_INBOX) {
            binding.toolbar.title = buildString { append("短信 - 收件箱") }
        } else {
            binding.toolbar.title = buildString { append("短信 - 发件箱") }
        }
        invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val tip = if (viewModel.boxType == MessageBoxType.TYPE_INBOX) {
            "切换至发件箱"
        } else {
            "切换至收件箱"
        }
        menu.add(tip)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
            .setOnMenuItemClickListener {
                viewModel.toggleBox()
                true
            }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<MessageEntity, *> {
        return MessageAdapter()
    }
}