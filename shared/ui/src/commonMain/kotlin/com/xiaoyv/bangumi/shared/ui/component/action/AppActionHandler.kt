@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.ui.component.action

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
import com.xiaoyv.bangumi.shared.component.ActionHandler
import com.xiaoyv.bangumi.shared.component.rememberActionHandler
import com.xiaoyv.bangumi.shared.core.types.BgmPathType
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.RakuenIdType
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.core.utils.toLongValue
import com.xiaoyv.bangumi.shared.core.utils.toUrl
import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import kotlinx.collections.immutable.persistentListOf

private val EmptyActionHandler = AppActionHandler(null)

val LocalActionHandler = staticCompositionLocalOf { EmptyActionHandler }

@Composable
fun rememberAppActionHandler(onNavScreen: (Screen) -> Unit): AppActionHandler {
    val currentOnNavScreen by rememberUpdatedState(onNavScreen)
    val actionHandler = rememberActionHandler()
    val handler = remember(actionHandler, currentOnNavScreen) { AppActionHandler(actionHandler) }

    DisposableEffect(handler, currentOnNavScreen) {
        handler.onNavScreen = currentOnNavScreen
        onDispose {
            handler.onNavScreen = null
        }
    }
    return handler
}

@Stable
class AppActionHandler(val actionHandler: ActionHandler?) {
    internal var onNavScreen: ((Screen) -> Unit)? = null

    private val handleHosts = persistentListOf("bgm.tv", "bangumi.tv", "chii.in")

    fun shareContent(content: String) = actionHandler?.shareContent(content)
    fun copyContent(content: String) = actionHandler?.copyContent(content)
    fun openInBrowser(link: String) = actionHandler?.openInBrowser(link)

    fun openImage(url: String) {
        onNavScreen?.invoke(Screen.PreviewMain(url))
    }

    fun openBgmLink(titleLink: String, jumpWeb: Boolean = true): Boolean {
        debugLog { "Handle Url: $titleLink" }

        val onNavScreen = onNavScreen ?: return false

        val url = if (titleLink.startsWith("http", true)) titleLink.toUrl() else (WebConstant.URL_BASE_WEB + titleLink).toUrl()
        val id = titleLink.substringAfterLast("/")
            .substringBefore("#")
            .substringBefore("?")

        val longId = id.toLongValue()

        // 无法处理的 URL 直接跳转内置浏览器
        if (!handleHosts.contains(url.host)) {
            if (jumpWeb) {
                onNavScreen(Screen.Web(url.toString()))
                return true
            }
            return false
        }

        when {
            // 话题
            titleLink.contains(BgmPathType.TYPE_TOPIC) -> {
                when {
                    // 虚拟人物
                    titleLink.contains(RakuenIdType.TYPE_CRT) -> {
                        onNavScreen(Screen.Article(longId, RakuenIdType.TYPE_CRT))
                        return true
                    }
                    // 章节
                    titleLink.contains(RakuenIdType.TYPE_EP) -> {
                        onNavScreen(Screen.Article(longId, RakuenIdType.TYPE_EP))
                        return true
                    }
                    // 小组
                    titleLink.contains(RakuenIdType.TYPE_GROUP) -> {
                        onNavScreen(Screen.Article(longId, RakuenIdType.TYPE_GROUP))
                        return true
                    }
                    // 现实人物
                    titleLink.contains(RakuenIdType.TYPE_PERSON) -> {
                        onNavScreen(Screen.Article(longId, RakuenIdType.TYPE_PERSON))
                        return true
                    }
                    // 条目
                    titleLink.contains(RakuenIdType.TYPE_SUBJECT) -> {
                        onNavScreen(Screen.Article(longId, RakuenIdType.TYPE_SUBJECT))
                        return true
                    }
                }
            }
            // 日志
            titleLink.contains(BgmPathType.TYPE_BLOG) -> {
                onNavScreen(Screen.Article(longId, RakuenIdType.TYPE_BLOG))
                return true
            }
            // 标签
            titleLink.contains(BgmPathType.TYPE_SEARCH_TAG) -> {
                onNavScreen(Screen.TagDetail())
                return true
            }
            // 虚拟角色
            titleLink.contains(BgmPathType.TYPE_CHARACTER) -> {
                onNavScreen(Screen.MonoDetail(longId, MonoType.CHARACTER))
                return true
            }
            // 现实人物
            titleLink.contains(BgmPathType.TYPE_PERSON) -> {
                onNavScreen(Screen.MonoDetail(longId, MonoType.PERSON))
                return true
            }
            // 用户
            titleLink.contains(BgmPathType.TYPE_USER) -> {
                onNavScreen(Screen.UserDetail(id))
                return true
            }
            // 条目
            titleLink.contains(BgmPathType.TYPE_SUBJECT) -> {
                when {
                    // 章节详情
                    titleLink.contains(RakuenIdType.TYPE_EP) -> {
                        onNavScreen(Screen.Article(longId, RakuenIdType.TYPE_EP))
                    }
                    // 条目详情
                    else -> onNavScreen(Screen.SubjectDetail(longId))
                }
                return true
            }
            // 小组
            titleLink.contains(BgmPathType.TYPE_GROUP) -> {
                onNavScreen(Screen.GroupDetail(id))
                return true
            }
            // 目录
            titleLink.contains(BgmPathType.TYPE_INDEX) -> {
                onNavScreen(Screen.IndexDetail(longId))
                return true
            }
        }

        return false
    }
}