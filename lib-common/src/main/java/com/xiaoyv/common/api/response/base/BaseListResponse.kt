package com.xiaoyv.common.api.response.base

import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
 * BaseListResponse
 *
 * @author why
 * @since 11/18/23
 */
@Keep
@Parcelize
class BaseListResponse<T> : BaseResponse<List<T>>()
