package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.MonoType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeMonoDisplay(
    @SerialName("type")
    @field:MonoType
    val type: Int = MonoType.UNKNOWN,

    @SerialName("info")
    val info: ComposeMonoInfo = ComposeMonoInfo.Empty,
) {
    val mono = if (type == MonoType.CHARACTER) info.mono else info.mono
    val id = mono.id

    val key = "$type-$id"

    companion object {
        val Empty = ComposeMonoDisplay()

        fun from(@MonoType type: Int, mono: ComposeMono): ComposeMonoDisplay {
            return ComposeMonoDisplay(type = type, info = ComposeMonoInfo(mono = mono))
        }
    }
}
