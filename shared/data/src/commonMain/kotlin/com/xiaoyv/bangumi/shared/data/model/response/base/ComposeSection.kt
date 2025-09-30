package com.xiaoyv.bangumi.shared.data.model.response.base

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class ComposeSection<T : Any>(
    @SerialName("key") val key: String = "",
    @SerialName("header") val header: ComposeSectionTitle = ComposeSectionTitle.Empty,
    @SerialName("item") val item: T,
) {
    val isHeader get() = header != ComposeSectionTitle.Empty
}

@Serializable
@Immutable
data class ComposeSectionTitle(
    @SerialName("id") val id: String = "",
    @SerialName("title") val title: String = "",
    @SerialName("subtitle") val subtitle: String = "",
    @SerialName("more") val more: String = "",
) {
    companion object {
        val Empty = ComposeSectionTitle()
    }
}