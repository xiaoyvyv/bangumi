package com.xiaoyv.bangumi.ui.feature.notify

import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListActivity
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.common.api.parser.entity.NotifyEntity
import com.xiaoyv.common.currentApplication
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.orEmpty

/**
 * Class: [NotifyActivity]
 *
 * @author why
 * @since 12/8/23
 */
class NotifyActivity : BaseListActivity<NotifyEntity, NotifyViewModel>() {
    override val isOnlyOnePage: Boolean
        get() = true

    override val toolbarTitle: String
        get() = "电波提醒"

    override fun initListener() {
        super.initListener()

        contentAdapter.setOnDebouncedChildClickListener(R.id.iv_avatar) {
            RouteHelper.jumpUserDetail(it.userId)
        }

        // 同意
        contentAdapter.setOnDebouncedChildClickListener(R.id.btn_action) {
            removeItemNotify(it)
            viewModel.addFriend(it.userId)
        }

        // 拒绝
        contentAdapter.setOnDebouncedChildClickListener(R.id.btn_ignore) {
            removeItemNotify(it)
        }

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_notify) {
            if (it.titleLink.isNotBlank()) {
                RouteHelper.handleUrl(it.titleLink)
            } else {
                RouteHelper.jumpUserDetail(it.userId)
            }
        }
    }

    /**
     * 取消红点
     */
    private fun removeItemNotify(entity: NotifyEntity) {
        entity.isAddFriend = false
        (contentAdapter as NotifyAdapter).notifyCount = 0
        val index = contentAdapter.itemIndexOfFirst(entity)
        if (index != -1) {
            contentAdapter.notifyItemChanged(index)
        }
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<NotifyEntity, *> {
        return NotifyAdapter(currentApplication.globalNotify.value?.first.orEmpty())
    }
}