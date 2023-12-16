package com.xiaoyv.bangumi.ui.feature.setting.block

import android.view.Menu
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListActivity
import com.xiaoyv.common.api.parser.entity.BlockEntity
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [BlockActivity]
 *
 * @author why
 * @since 12/17/23
 */
class BlockActivity : BaseListActivity<BlockEntity, BlockViewModel>() {
    override val isOnlyOnePage: Boolean
        get() = true

    override val toolbarTitle: String
        get() = "绝交的用户"

    override fun initListener() {
        super.initListener()

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_friend) {
            showConfirmDialog(message = "是否和该用户冰释前嫌？", onConfirmClick = {
                viewModel.release(it.numberId)
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add("什么是绝交？")
            .setOnMenuItemClickListener {
                showConfirmDialog(
                    title = "用户绝交",
                    message = "与用户绝交后，不再看到用户的所有话题、评论、日志、私信、提醒",
                    cancelText = null,
                    confirmText = "知道了"
                )
                true
            }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<BlockEntity, *> {
        return BlockAdapter()
    }
}