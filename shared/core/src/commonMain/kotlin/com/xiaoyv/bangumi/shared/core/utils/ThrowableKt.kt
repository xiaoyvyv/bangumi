package com.xiaoyv.bangumi.shared.core.utils

import com.xiaoyv.bangumi.shared.core.exception.ApiException
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.CancellationException

val Throwable?.errMsg: String
    get() {
        if (this != null) debugLog { this@errMsg }
        return when (this) {
            is RedirectResponseException -> "请求被重定向"
            is ClientRequestException -> "客户端请求错误"
            is ServerResponseException -> "服务器内部错误"
            is ResponseException -> "HTTP响应异常"
            is ConnectTimeoutException -> "网络异常，请检查连接"
            is SocketTimeoutException -> "网络异常，请检查连接"
            is CancellationException -> "请求已取消"
            is NullPointerException -> "空指针异常"
            is IllegalArgumentException -> "参数错误"
            is ApiException -> message
            else -> this?.message ?: "未知错误"
        }
    }

fun Throwable?.printTrace() {
    println("Error: $errMsg")
}
