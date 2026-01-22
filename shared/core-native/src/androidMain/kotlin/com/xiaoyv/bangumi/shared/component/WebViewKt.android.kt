package com.xiaoyv.bangumi.shared.component

import com.multiplatform.webview.web.NativeWebView

actual fun NativeWebView.configWebSettings() {
    settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
}