package com.xiaoyv.bangumi.shared.data.model.response.bgm.pm

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposePmThread(
    val id: Long = 0,
    val name: String = ""
) {
    companion object {
        val Empty = ComposePmThread()
    }
}
