package com.xiaoyv.bangumi.ui.feature.web

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup.MarginLayoutParams
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebView.HitTestResult
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.google.android.material.appbar.AppBarLayout.ScrollingViewBehavior
import com.xiaoyv.bangumi.databinding.ActivityWebBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.binding.BaseBindingActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.widget.webview.listener.OnWebLoadListener
import com.xiaoyv.widget.webview.listener.OnWindowListener


/**
 * Class: [WebActivity]
 *
 * @author why
 * @since 12/10/23
 */
class WebActivity : BaseBindingActivity<ActivityWebBinding>() {
    private var url = ""
    private var injectJs = ""
    private var fitToolbar = false

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        url = bundle.getString(NavKey.KEY_STRING).orEmpty()
        injectJs = bundle.getString(NavKey.KEY_STRING_SECOND).orEmpty()
        fitToolbar = bundle.getBoolean(NavKey.KEY_BOOLEAN, false)
    }

    override fun initView() {
        binding.toolbar.initNavBack(this)

        binding.webView.bindTitleToolbar(binding.toolbar)
        binding.webView.bindWebProgress(binding.pbProgress)
        binding.webView.multipleWindows = true

        if (fitToolbar) {
            binding.webView.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                behavior = ScrollingViewBehavior()
            }
        }
    }

    override fun initData() {
        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            BgmApiManager.readBgmCookie().forEach {
                setCookie(BgmApiManager.URL_BASE_WEB, it.toString())
                flush()
            }
        }
        binding.webView.loadUrl(url)
        binding.webView.onWebLoadListener = object : OnWebLoadListener {
            override fun onLoadFinish(webView: WebView, url: String) {
                if (injectJs.isNotBlank()) {
                    debugLog { "Inject: $injectJs" }
                    binding.webView.evaluateJavascript(injectJs, null)
                }
            }
        }
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

        // 设置 WebView 的长按事件监听器
        binding.webView.setOnLongClickListener {
            val result = binding.webView.getHitTestResult()
            if (result.type != 0) {
                handlerLongPress(result.type, result.extra.orEmpty())
            }
            true
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.webView) { _, i ->
            val ime = i.getInsets(WindowInsetsCompat.Type.ime())
            binding.webView.updateLayoutParams<MarginLayoutParams> {
                bottomMargin = ime.bottom - ime.top
            }
            i
        }
        ViewCompat.requestApplyInsets(binding.webView)
    }

    private fun handlerLongPress(type: Int, extra: String) {
        when (type) {
            // 图片
            HitTestResult.IMAGE_TYPE -> {
                // Dollars 头像长按
                if (url.contains("dollars")) {
                    val userId = extra.substringAfterLast("/").substringBefore(".")
                    RouteHelper.jumpUserDetail(userId)
                }
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