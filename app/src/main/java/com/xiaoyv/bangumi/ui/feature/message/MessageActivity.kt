package com.xiaoyv.bangumi.ui.feature.message

import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListActivity
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.common.api.parser.entity.MessageEntity
import com.xiaoyv.common.config.annotation.MessageBoxType
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.dialog.UiDialog
import com.xiaoyv.widget.stateview.StateViewLiveData

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

    override fun LifecycleOwner.initViewObserverExt() {
        viewModel.loadingViewState.observe(this) {
            invalidateOptionsMenu()
        }
    }

    override fun onListDataFinish(list: List<MessageEntity>) {
        if (viewModel.boxType == MessageBoxType.TYPE_INBOX) {
            binding.toolbar.title = buildString { append("短信 - 收件箱") }
        } else {
            binding.toolbar.title = buildString { append("短信 - 发件箱") }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val tip = if (viewModel.boxType == MessageBoxType.TYPE_INBOX) {
            "切换至发件箱"
        } else {
            "切换至收件箱"
        }
        menu.add(tip)
            .setEnabled(viewModel.loadingViewState.value?.type != StateViewLiveData.StateType.STATE_LOADING)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
            .setOnMenuItemClickListener {
                viewModel.toggleBox()
                true
            }

        // 有数据显示清空菜单
        if (!viewModel.onListLiveData.value.isNullOrEmpty()) {
            menu.add("清空当前页")
                .setEnabled(viewModel.loadingViewState.value?.type != StateViewLiveData.StateType.STATE_LOADING)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
                .setOnMenuItemClickListener {
                    showConfirmDialog(message = "是否清空当前显示的全部短信？", onConfirmClick = {
                        viewModel.clearBox()
                    })
                    true
                }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<MessageEntity, *> {
        return MessageAdapter()
    }
}