package com.xiaoyv.bangumi.shared.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.UriHandler

import io.github.vinceglb.filekit.PlatformFile

@Composable
expect fun rememberActionHandler(): ActionHandler

@Stable
expect class ActionHandler(uriHandler: UriHandler, clipboard: Clipboard?) {

    fun shareContent(content: String)

    fun copyContent(content: String)

    fun openInBrowser(link: String)

    fun saveMedia(file: PlatformFile)

    fun shareMedia(file: PlatformFile)

    fun setWallpaper(file: PlatformFile)
}


