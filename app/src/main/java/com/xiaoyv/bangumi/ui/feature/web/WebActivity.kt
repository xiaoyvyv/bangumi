package com.xiaoyv.bangumi.ui.feature.web

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.xiaoyv.bangumi.databinding.ActivityWebBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.binding.BaseBindingActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.widget.webview.listener.OnWindowListener

/**
 * Class: [WebActivity]
 *
 * @author why
 * @since 12/10/23
 */
class WebActivity : BaseBindingActivity<ActivityWebBinding>() {
    private var url = ""

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        url = bundle.getString(NavKey.KEY_STRING).orEmpty()
    }

    override fun initView() {
        binding.toolbar.initNavBack(this)

        binding.webView.bindTitleToolbar(binding.toolbar)
        binding.webView.bindWebProgress(binding.pbProgress)
        binding.webView.multipleWindows = true
    }

    override fun initData() {
        binding.webView.loadUrl(url)
    }

    override fun initListener() {
        binding.webView.onWindowListener = object : OnWindowListener {
            override fun openNewWindow(url: String) {
                if (RouteHelper.handleUrl(url)) {
                    return
                }
                RouteHelper.jumpWeb(url)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.webView.destroy()
    }
}