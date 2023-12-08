package com.xiaoyv.bangumi.ui.feature.notify

import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListActivity
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.common.api.parser.entity.NotifyEntity
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

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

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_notify) {
            if (it.titleLink.isNotBlank()) {
                RouteHelper.handleUrl(it.titleLink)
            }
        }
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<NotifyEntity, *> {
        return NotifyAdapter()
    }
}