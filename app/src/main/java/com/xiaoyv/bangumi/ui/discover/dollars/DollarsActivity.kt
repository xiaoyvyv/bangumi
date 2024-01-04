package com.xiaoyv.bangumi.ui.discover.dollars

import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
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
import com.xiaoyv.common.api.parser.entity.DollarsEntity
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.kts.dpi
import com.xiaoyv.widget.kts.getAttrColor
import kotlin.math.max

/**
 * Class: [DollarsActivity]
 *
 * @author why
 * @since 1/4/24
 */
class DollarsActivity : BaseListActivity<DollarsEntity, DollarsViewModel>() {
    private lateinit var inputBinding: ActivityMessageDetailInputBinding

    private val rootView: FrameLayout
        get() = findViewById(android.R.id.content)

    override val isOnlyOnePage: Boolean
        get() = true

    override val needResetPositionWhenRefresh: Boolean
        get() = false

    override val hideInputBoardWhenTouchItem: Boolean
        get() = false

    override val toolbarTitle: String
        get() = "Dollars"

    override fun initView() {
        super.initView()

        binding.root.setBackgroundColor(getAttrColor(GoogleAttr.colorSurfaceVariant))
        binding.rvContent.updatePadding(bottom = 40.dpi)
    }

    override fun injectFilter(container: FrameLayout) {
        inputBinding = ActivityMessageDetailInputBinding.inflate(layoutInflater)
        inputBinding.etMessage.hint = ""

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

        inputBinding.etMessage.hint = "和 Bangumi 娘聊天吧..."
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

        // @ 操作
        contentAdapter.addOnItemChildLongClickListener(R.id.iv_avatar_theme) { adapter, v, position ->
            val entity = adapter.getItem(position)
                ?: return@addOnItemChildLongClickListener true

            val at = entity.uid.let { uid -> if (uid == 0L) "@Bangumi娘 " else "@$uid " }
            val tmp = inputBinding.etMessage.text.toString().trim()
            if (!tmp.startsWith(at)) {
                inputBinding.etMessage.setText(buildString {
                    append(at)
                    append(tmp)
                })

                runCatching { inputBinding.etMessage.setSelection(inputBinding.etMessage.length()) }

                KeyboardUtils.showSoftInput(inputBinding.etMessage)
            }
            true
        }

        contentAdapter.setOnDebouncedChildClickListener(R.id.iv_avatar_theme) {
            if (it.uid != 0L) {
                RouteHelper.jumpUserDetail(it.uid.toString())
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(inputBinding.root) { _, insets ->
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val imeHeight = ime.bottom - ime.top

            val navigation = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            val navigationHeight = navigation.bottom - navigation.top
            val bottomInset = max(imeHeight, navigationHeight)

            inputBinding.root.updatePadding(bottom = bottomInset)
            binding.srlRefresh.updatePadding(bottom = bottomInset + inputBinding.etMessage.height + 24.dpi)

            if (imeHeight > 0) smoothScrollToBottom()

            insets
        }
    }

    override fun onListDataFinish(list: List<DollarsEntity>) {
        if (viewModel.needScrollToBottom) {
            scrollToBottom()
        }

        if (viewModel.needSmoothScrollToBottom) {
            viewModel.needSmoothScrollToBottom = false
            smoothScrollToBottom()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add("提示")
            .setIcon(CommonDrawable.ic_help)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            .setOnMenuItemClickListener {
                showConfirmDialog(message = "长按头像可以 @Bangumi 娘", cancelText = null)
                true
            }
        return super.onCreateOptionsMenu(menu)
    }

    override fun canShowStateLoading(): Boolean {
        return false
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<DollarsEntity, *> {
        return DollarsAdapter()
    }
}