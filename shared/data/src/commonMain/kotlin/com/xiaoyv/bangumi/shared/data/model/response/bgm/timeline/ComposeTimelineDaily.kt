package com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import com.xiaoyv.bangumi.shared.core.types.TimelineDailyAction
import com.xiaoyv.bangumi.shared.core.utils.addUrl
import com.xiaoyv.bangumi.shared.core.utils.forEachWithSeparator
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeGroup
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeTimelineDaily(
    @SerialName("groups") val groups: SerializeList<ComposeGroup> = persistentListOf(),
    @SerialName("users") val users: SerializeList<ComposeUser> = persistentListOf(),
) {

    @Composable
    fun rememberTimelineTitle(
        @TimelineDailyAction action: Int,
        highlightColor: Color = MaterialTheme.colorScheme.primary,
        onUserClickListener: (ComposeUser) -> Unit = { },
        onGroupClickListener: (ComposeGroup) -> Unit = { },
    ): AnnotatedString = remember(this, action, highlightColor, onUserClickListener, onGroupClickListener) {
        buildAnnotatedString {
            when (action) {
                TimelineDailyAction.ADD_FRIEND -> users.forEachWithSeparator(
                    onStart = { append("将 ") },
                    onItem = { user ->
                        addUrl(
                            text = user.username,
                            style = SpanStyle(color = highlightColor, textDecoration = TextDecoration.Underline),
                            listener = { onUserClickListener(user) }
                        )
                    },
                    onSeparator = { append("、") },
                    onEnd = { append(" 加为了好友") }
                )

                TimelineDailyAction.CREATE_GROUP,
                TimelineDailyAction.JOIN_GROUP -> groups.forEachWithSeparator(
                    onStart = { append(if (action == TimelineDailyAction.CREATE_GROUP) "创建了 " else "加入了 ") },
                    onItem = { group ->
                        addUrl(
                            text = group.title,
                            style = SpanStyle(color = highlightColor, textDecoration = TextDecoration.Underline),
                            listener = { onGroupClickListener(group) }
                        )
                    },
                    onSeparator = { append("、") },
                    onEnd = { append(" 等小组") }
                )

                TimelineDailyAction.REGISTER -> append("注册了账号")
                TimelineDailyAction.JOIN_PARK -> append("加入了乐园")
                else -> append("神秘的行动")
            }
        }
    }

    companion object {
        val Empty = ComposeTimelineDaily()
    }
}
