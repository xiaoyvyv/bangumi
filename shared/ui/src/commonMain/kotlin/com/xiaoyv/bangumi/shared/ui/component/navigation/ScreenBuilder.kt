package com.xiaoyv.bangumi.shared.ui.component.navigation

import com.xiaoyv.bangumi.shared.core.utils.RouteBuilder
import com.xiaoyv.bangumi.shared.core.utils.toJson
import io.ktor.util.encodeBase64

/**
 * [Screen.SubjectBrowser] 填充跳转参数
 */
fun RouteBuilder.withScreenParams(screen: Screen.SubjectBrowser): RouteBuilder {
    param(EXTRA_OBJ, screen.body.toJson().encodeBase64())
    param(EXTRA_TITLE, screen.title)
    return this
}

/**
 * [Screen.SubjectBrowser] 填充跳转参数
 */
fun RouteBuilder.withScreenParams(screen: Screen.TagDetail): RouteBuilder {
    param(EXTRA_TYPE, screen.type)
    return this
}

/**
 * [Screen.SubjectBrowser] 填充跳转参数
 */
fun RouteBuilder.withScreenParams(screen: Screen.Calendar): RouteBuilder {
    param(EXTRA_BOOLEAN, screen.isToday)
    return this
}
