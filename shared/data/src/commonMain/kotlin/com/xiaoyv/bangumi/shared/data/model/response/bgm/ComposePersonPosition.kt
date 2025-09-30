package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposePersonPosition(
    @SerialName("summary") val summary: String = "",
    @SerialName("type") val type: ComposePersonPersonType = ComposePersonPersonType.Empty,
) {
    companion object {
        val All = ComposePersonPosition(
            type = ComposePersonPersonType(
                cn = "全部",
                jp = "全",
                en = "All"
            )
        )
    }
}

@Immutable
@Serializable
data class ComposePersonPersonType(
    @SerialName("cn") val cn: String = "",
    @SerialName("en") val en: String = "",
    @SerialName("id") val id: Long = 0,
    @SerialName("jp") val jp: String = "",
) {
    val displayName: String get() = cn.ifBlank { en }.ifBlank { jp }

    companion object {
        val Empty = ComposePersonPersonType()

        fun from(id: Long, text: String): ComposePersonPersonType {
            return ComposePersonPersonType(
                cn = text,
                en = text,
                jp = text,
                id = id
            )
        }
    }
}

