package com.xiaoyv.bangumi.ui.feature.message.detail

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnNextLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import com.blankj.utilcode.util.KeyboardUtils
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListActivity
import com.xiaoyv.bangumi.databinding.ActivityMessageDetailInputBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.MessageEntity
import com.xiaoyv.common.helper.NotifyHelper
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.callback.setOnFastLimitClickListener

/**
 * Class: [MessageDetailActivity]
 *
 * @author why
 * @since 12/8/23
 */
class MessageDetailActivity : BaseListActivity<MessageEntity, MessageDetailViewModel>() {
    private lateinit var inputBinding: ActivityMessageDetailInputBinding

    override val isOnlyOnePage: Boolean
        get() = true

    override val needResetPositionWhenRefresh: Boolean
        get() = false

    override val toolbarTitle: String
        get() = viewModel.fromName.ifBlank { "短信详情" }

    private val rootView: FrameLayout
        get() = findViewById(android.R.id.content)

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.messageId = bundle.getString(NavKey.KEY_STRING).orEmpty()
        viewModel.fromName = bundle.getString(NavKey.KEY_STRING_SECOND).orEmpty()
    }

    override fun injectFilter(container: FrameLayout) {
        inputBinding = ActivityMessageDetailInputBinding.inflate(layoutInflater)
        inputBinding.etMessage.hint = buildString {
            append("回复：")
            append(viewModel.fromName)
        }
        rootView.addView(
            inputBinding.root, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.BOTTOM
            }
        )

        // 更新 padding
        inputBinding.root.doOnNextLayout {
            binding.srlRefresh.updatePadding(bottom = inputBinding.root.height)
        }
    }

    override fun initListener() {
        super.initListener()

        inputBinding.etMessage.doAfterTextChanged {
            val input = it.toString().trim()
            val lineCount = inputBinding.etMessage.lineCount
            inputBinding.btnSend.updateLayoutParams<ConstraintLayout.LayoutParams> {
                verticalBias = if (lineCount == 1) 0.5f else 1f
            }
            inputBinding.btnSend.isVisible = input.isNotBlank()
        }

        inputBinding.btnSend.setOnFastLimitClickListener {
            val input = inputBinding.etMessage.text.toString().trim()
            viewModel.sendMessage(input)

            inputBinding.etMessage.text = null
        }

        contentAdapter.setOnDebouncedChildClickListener(R.id.iv_avatar_theme) {
            RouteHelper.jumpUserDetail(it.fromId)
        }

        ViewCompat.setOnApplyWindowInsetsListener(inputBinding.root) { _, insets ->
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            viewModel.imeHeight = ime.bottom - ime.top
            rootView.updatePadding(bottom = viewModel.imeHeight)
            binding.srlRefresh.updatePadding(bottom = inputBinding.root.height)
            if (viewModel.imeHeight > 0) {
                smoothScrollToBottom()
            }
            insets
        }
    }

    override fun onListDataFinish(list: List<MessageEntity>) {
        inputBinding.root.isVisible = true
        
        // 软键盘打开的情况下，轮询的消息才自动滑动
        if (viewModel.imeHeight > 0 || !viewModel.isPollMessages) scrollToBottom()

        // 检测是否可以回复
        if (viewModel.replyForm.isEmpty()) {
            inputBinding.etMessage.text = null
            inputBinding.etMessage.hint = "对方回复你后才可以继续发消息"
            inputBinding.etMessage.isEnabled = false
            KeyboardUtils.hideSoftInput(this)
        } else {
            inputBinding.etMessage.hint = buildString {
                append("回复：")
                append(viewModel.fromName)
            }
            inputBinding.etMessage.isEnabled = true
        }

        // 刷新短信数目
        NotifyHelper.markMessageRead()
    }

    override fun onListDataError() {
        inputBinding.root.isVisible = false
    }

    /**
     * 轮询的请求不显示 loading 状态图
     */
    override fun canShowStateLoading(): Boolean {
        return viewModel.isPollMessages.not()
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<MessageEntity, *> {
        return MessageDetailAdapter()
    }
}