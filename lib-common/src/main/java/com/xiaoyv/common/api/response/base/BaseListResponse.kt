package com.xiaoyv.common.api.response.base

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * BaseListResponse
 *
 * @author why
 * @since 11/18/23
 */
@Keep
@Parcelize
class BaseListResponse<T>(
    @SerializedName("total") var total: Int = 0,
    @SerializedName("limit") var limit: Int = 0,
    @SerializedName("offset") var offset: Int = 0,
) : BaseResponse<List<T>>()
