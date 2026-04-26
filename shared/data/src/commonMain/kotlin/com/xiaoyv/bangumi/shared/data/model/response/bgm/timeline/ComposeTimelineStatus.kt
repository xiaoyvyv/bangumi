@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.xiaoyv.bangumi.shared.core.types.TimelineStatusAction
import com.xiaoyv.bangumi.shared.core.utils.withSpanStyle
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeTimelineStatus(
    @SerialName("nickname") val nickname: ComposeTimelineNickname = ComposeTimelineNickname.Empty,
    @SerialName("sign") val sign: String = "",
    @SerialName("tsukkomi") val tsukkomi: String = "",
) {

    @Composable
    fun rememberTimelineTitle(
        @TimelineStatusAction action: Int,
        highlightColor: Color = MaterialTheme.colorScheme.primary,
    ): AnnotatedString = remember(this, action, highlightColor) {
        buildAnnotatedString {
            when (action) {
                TimelineStatusAction.UPDATE_SIGN -> {
                    append("更新了签名：")
                    withSpanStyle(color = highlightColor, textDecoration = TextDecoration.Underline) {
                        append(sign)
                    }
                }

                TimelineStatusAction.RENAME -> {
                    append("从 ")
                    withSpanStyle(
                        color = highlightColor,
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.SemiBold
                    ) {
                        append(nickname.before)
                    }
                    append(" 改名为 ")
                    withSpanStyle(
                        color = highlightColor,
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.SemiBold
                    ) {
                        append(nickname.after)
                    }
                }

                TimelineStatusAction.COMMENT -> append(tsukkomi)
            }
        }
    }

    companion object {
        val Empty = ComposeTimelineStatus()
    }
}