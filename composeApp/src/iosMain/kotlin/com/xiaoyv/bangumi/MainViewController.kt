@file:Suppress("FunctionName", "unused")

package com.xiaoyv.bangumi

import androidx.compose.ui.window.ComposeUIViewController
import com.xiaoyv.bangumi.shared.component.ExternalUriHandler

fun MainViewController() = ComposeUIViewController { App() }

fun onImageReceived(path: String) = ExternalUriHandler.onImageReceived(path)