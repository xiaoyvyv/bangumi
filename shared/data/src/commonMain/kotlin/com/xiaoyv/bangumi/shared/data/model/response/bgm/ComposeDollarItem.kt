package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import com.xiaoyv.bangumi.shared.core.utils.parseHtmlHexColor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Immutable
@Serializable
data class ComposeDollarItem(
    @SerialName("avatar") val avatar: String = "",
    @SerialName("color") val color: String = "",
    @SerialName("id") val id: String = "",
    @SerialName("is_html") val isHtml: Int = 0,
    @SerialName("msg") val msg: String = "",
    @SerialName("nickname") val nickname: String = "",
    @SerialName("timestamp") val timestamp: Long = 0,
    @SerialName("uid") val uid: Long = 0,

    @Transient
    val contentHtml: AnnotatedString = AnnotatedString(""),
) {
    @Composable
    fun displayColor(): Color {
        return remember(color) { parseHtmlHexColor(color) } ?: MaterialTheme.colorScheme.primary
    }
}