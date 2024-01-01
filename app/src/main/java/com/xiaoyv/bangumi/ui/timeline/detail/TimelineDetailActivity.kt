package com.xiaoyv.bangumi.ui.timeline.detail

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.QuickAdapterHelper
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListActivity
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.TimelineReplyEntity
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.kts.showInputDialog
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.toast
import com.xiaoyv.widget.kts.useNotNull

/**
 * Class: [TimelineDetailActivity]
 *
 * @author why
 * @since 1/1/24
 */
class TimelineDetailActivity : BaseListActivity<TimelineReplyEntity, TimelineDetailViewModel>() {
    private val timelineHeader by lazy {
        TimelineDetailHeader()
    }

    override val toolbarTitle: String
        get() = "吐槽详情"

    override val isOnlyOnePage: Boolean
        get() = true

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.timelineId = bundle.getString(NavKey.KEY_STRING).orEmpty()
    }

    override fun initAdapter() {
        binding.rvContent.adapter = adapterHelper.adapter
    }

    override fun initListener() {
        super.initListener()

        timelineHeader.setOnDebouncedChildClickListener(R.id.layout_timeline) {
            showSubReply(it.detail.name, "")
        }

        timelineHeader.setOnDebouncedChildClickListener(R.id.iv_avatar) {
            RouteHelper.jumpUserDetail(it.detail.userId)
        }

        contentAdapter.setOnDebouncedChildClickListener(R.id.iv_comment) {
            useNotNull(viewModel.onTimelineDetailLiveData.value) {
                showSubReply(detail.name, it.id)
            }
        }
    }

    override fun onCreateContentAdapterHelper(): QuickAdapterHelper {
        return QuickAdapterHelper.Builder(contentAdapter)
            .build()
            .addBeforeAdapter(0, timelineHeader)
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<TimelineReplyEntity, *> {
        return TimelineDetailAdapter()
    }

    override fun LifecycleOwner.initViewObserverExt() {
        viewModel.onTimelineDetailLiveData.observe(this) {
            if (it != null) timelineHeader.submitList(listOf(it))
        }
    }

    /**
     * 回复吐槽
     */
    private fun showSubReply(userUserName: String, atUserId: String) {
        if (!UserHelper.isLogin) {
            toast("你还没有登录呢")
            return
        }

        showInputDialog(
            title = "$userUserName 的吐槽",
            inputHint = "吐槽回复",
            minLines = 3,
            default = if (atUserId.isNotBlank()) "@$atUserId " else "",
            onInput = { content ->
                viewModel.replyTimeline(content)
            }
        )
    }
}