package com.xiaoyv.bangumi.shared.core.exception

import kotlinx.io.IOException

/**
 * [ApiException]
 *
 * @author why
 * @since 2025/1/14
 */
open class ApiException(
    override val message: String = "Api Error",
    cause: Throwable? = null,
) : IOException(message, cause)

