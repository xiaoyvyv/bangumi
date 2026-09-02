package com.xiaoyv.bangumi.shared.data.workflow.node.effect

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionSideEffect

/**
 * 请求宿主使用系统浏览器打开网页的副作用。
 *
 * @param url 已由节点解析并通过 URL 策略校验的绝对网页地址。
 */
@Immutable
data class ActionOpenExternalUrlEffect(
    /**
     * 要在外部浏览器中打开的绝对网页地址。
     */
    val url: String,
) : ActionSideEffect

/**
 * 请求宿主通过 URI 跳转外部应用的副作用。
 *
 * @param uri 目标应用可处理的 URI。
 * @param fallbackUrl 目标应用不可用时可选的网页降级地址。
 */
@Immutable
data class ActionOpenExternalAppEffect(
    /**
     * 目标应用可处理的 URI。
     */
    val uri: String,
    /**
     * 目标应用不可用时可选的网页降级地址。
     */
    val fallbackUrl: String? = null,
) : ActionSideEffect

/**
 * 请求宿主在应用内网页容器打开地址的副作用。
 *
 * @param url 已解析的绝对网页地址。
 */
@Immutable
data class ActionOpenInternalWebEffect(
    /**
     * 要在应用内网页容器打开的绝对网页地址。
     */
    val url: String,
) : ActionSideEffect
