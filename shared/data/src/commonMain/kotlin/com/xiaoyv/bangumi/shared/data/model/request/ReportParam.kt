@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.model.request

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.ReportReason
import com.xiaoyv.bangumi.shared.core.types.ReportType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ReportParam(
    @SerialName("id") val targetId: Long = 0,
    @SerialName("type") @field:ReportType val type: Int = ReportType.UNKNOWN,
    @SerialName("value") @field:ReportReason val value: Int = ReportReason.UNKNOWN,
    @SerialName("comment") val comment: String = "",
    @SerialName("formhash") val formhash: String = "",
    @SerialName("update") val update: String = "报告疑虑",
) {

    companion object {
        val Empty = ReportParam()
    }
}
