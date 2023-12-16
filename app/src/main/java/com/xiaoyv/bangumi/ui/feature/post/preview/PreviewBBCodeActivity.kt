package com.xiaoyv.bangumi.ui.feature.post.preview

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.databinding.ActivityPreviewBbcodeBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.common.widget.web.page.BBCodeView
import com.xiaoyv.widget.dialog.UiDialog

/**
 * Class: [PreviewBBCodeActivity]
 *
 * @author why
 * @since 12/16/23
 */
class PreviewBBCodeActivity :
    BaseViewModelActivity<ActivityPreviewBbcodeBinding, PreviewBBCodeViewModel>() {

    private val previewWeb by lazy {
        BBCodeView(binding.webView)
    }


    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.code = bundle.getString(NavKey.KEY_STRING).orEmpty()
    }

    override fun initView() {
        previewWeb.startLoad()
        binding.toolbar.initNavBack(this)
    }

    override fun initData() {
        launchUI(stateView = viewModel.loadingViewState) {
            previewWeb.setCode(viewModel.code)
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        binding.stateView.initObserver(this, viewModel.loadingViewState)
    }

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}