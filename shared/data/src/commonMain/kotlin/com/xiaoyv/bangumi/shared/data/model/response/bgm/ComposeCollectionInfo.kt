@file:OptIn(ExperimentalSerializationApi::class)

package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Immutable
@Serializable
data class ComposeCollectionInfo(
    @JsonNames("1", "wish") @SerialName("wish") val wish: Int = 0,
    @JsonNames("2", "collect") @SerialName("collect") val collect: Int = 0,
    @JsonNames("3", "doing") @SerialName("doing") val doing: Int = 0,
    @JsonNames("4", "on_hold") @SerialName("on_hold") val onHold: Int = 0,
    @JsonNames("5", "dropped") @SerialName("dropped") val dropped: Int = 0,
) {
    val total: Int
        get() = wish + collect + doing + onHold + dropped

    companion object {
        val Empty = ComposeCollectionInfo()
    }
}