package com.xiaoyv.bangumi.shared.data.model.request

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.ReportReason
import com.xiaoyv.bangumi.shared.core.types.ReportType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 
 *
 * @param type
 * @param id 被举报对象的 ID
 * @param reason 原因分类
 * @param comment 举报说明（可选）
 */
@Immutable
@Serializable
data class CreateReportParam(
    @SerialName(value = "id") val id: Long,
    @SerialName(value = "type") @ReportType val type: Int,
    @SerialName(value = "value") @ReportReason val reason: Int,
    @SerialName(value = "comment") val comment: String? = null
) 