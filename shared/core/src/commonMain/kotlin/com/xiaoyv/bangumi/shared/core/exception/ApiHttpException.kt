package com.xiaoyv.bangumi.shared.core.exception

open class ApiHttpException(
    val code: Int = 400,
    val errorMsg: String? = null,
) : ApiException(message = errorMsg ?: "HTTP $code")