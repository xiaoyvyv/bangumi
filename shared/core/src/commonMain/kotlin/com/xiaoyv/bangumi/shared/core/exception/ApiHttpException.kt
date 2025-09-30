package com.xiaoyv.bangumi.shared.core.exception

open class ApiHttpException(
    val code: Int = 400,
    val bodyAsText: String = "",
) : ApiException(message = "HTTP $code")