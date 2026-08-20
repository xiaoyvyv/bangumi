package com.xiaoyv.bangumi.shared.data.model.response.bgm.user

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_accept_friend
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_blog_post_reply
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_blog_reply
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_blog_reply_at
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_character_patch_accepted
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_character_patch_expired
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_character_patch_rejected
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_character_patch_reply
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_character_post_reply
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_character_reply_at
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_character_topic_reply
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_ep_post_reply
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_ep_reply_at
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_episode_patch_accepted
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_episode_patch_expired
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_episode_patch_rejected
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_episode_patch_reply
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_group_post_reply
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_group_topic_reply
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_group_topic_reply_at
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_index_post_reply
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_index_reply
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_person_patch_accepted
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_person_patch_expired
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_person_patch_rejected
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_person_patch_reply
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_person_post_reply
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_person_reply_at
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_request_friend
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_subject_patch_accepted
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_subject_patch_expired
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_subject_patch_rejected
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_subject_patch_reply
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_subject_post_reply
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_subject_topic_reply
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_subject_topic_reply_at
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_timeline_say_at
import com.xiaoyv.bangumi.core_resource.resources.notice_msg_timeline_say_reply
import com.xiaoyv.bangumi.shared.core.types.NoticeType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeDateLong
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * @param id
 * @param type 类型
 * @param sender
 * @param title
 * @param mainID 对应的 topicID, episodeID, userID ...
 * @param relatedID 对应的 postID ...
 * @param createdAt
 * @param unread
 */
@Immutable
@Serializable
data class ComposeNotice(
    @SerialName(value = "id") val id: Long = 0,
    @SerialName(value = "type") @NoticeType val type: Int = 0,
    @SerialName(value = "sender") val sender: ComposeUser = ComposeUser.Empty,
    @SerialName(value = "title") val title: String = "",
    @SerialName(value = "mainID") val mainID: Long = 0,
    @SerialName(value = "relatedID") val relatedID: Long = 0,
    @SerialName(value = "createdAt") val createdAt: SerializeDateLong = 0,
    @SerialName(value = "unread") val unread: Boolean = false
) {
    fun normalized() = this

    companion object {
        val Empty = ComposeNotice()
    }
}

/**
 * 构建带主色高亮 title 的 [AnnotatedString]。
 *
 * 模板字符串中 `%1$s` 占位符会被 [title] 替换，替换后的部分以 [titleColor] 高亮显示。
 */
@Composable
fun ComposeNotice.rememberDisplayAnnotatedString(
    titleColor: Color = MaterialTheme.colorScheme.primary,
): AnnotatedString {
    val template: StringResource? = when (type) {
        // 小组
        NoticeType.GROUP_TOPIC_REPLY -> Res.string.notice_msg_group_topic_reply
        NoticeType.GROUP_POST_REPLY -> Res.string.notice_msg_group_post_reply

        // 日志
        NoticeType.BLOG_REPLY -> Res.string.notice_msg_blog_reply
        NoticeType.BLOG_POST_REPLY -> Res.string.notice_msg_blog_post_reply

        // 角色|人物
        NoticeType.CHARACTER_TOPIC_REPLY -> Res.string.notice_msg_character_topic_reply

        NoticeType.CHARACTER_POST_REPLY -> Res.string.notice_msg_character_post_reply
        NoticeType.PERSON_POST_REPLY -> Res.string.notice_msg_person_post_reply

        // 条目
        NoticeType.SUBJECT_TOPIC_REPLY -> Res.string.notice_msg_subject_topic_reply
        NoticeType.SUBJECT_POST_REPLY -> Res.string.notice_msg_subject_post_reply

        // 好友
        NoticeType.REQUEST_FRIEND -> Res.string.notice_msg_request_friend
        NoticeType.ACCEPT_FRIEND -> Res.string.notice_msg_accept_friend

        // 目录
        NoticeType.INDEX_REPLY -> Res.string.notice_msg_index_reply
        NoticeType.INDEX_POST_REPLY -> Res.string.notice_msg_index_post_reply

        NoticeType.EP_POST_REPLY -> Res.string.notice_msg_ep_post_reply

        // 条目修订
        NoticeType.SUBJECT_PATCH_ACCEPTED -> Res.string.notice_msg_subject_patch_accepted
        NoticeType.SUBJECT_PATCH_REJECTED -> Res.string.notice_msg_subject_patch_rejected
        NoticeType.SUBJECT_PATCH_EXPIRED -> Res.string.notice_msg_subject_patch_expired
        NoticeType.SUBJECT_PATCH_REPLY -> Res.string.notice_msg_subject_patch_reply

        // 章节修订
        NoticeType.EPISODE_PATCH_ACCEPTED -> Res.string.notice_msg_episode_patch_accepted
        NoticeType.EPISODE_PATCH_REJECTED -> Res.string.notice_msg_episode_patch_rejected
        NoticeType.EPISODE_PATCH_EXPIRED -> Res.string.notice_msg_episode_patch_expired
        NoticeType.EPISODE_PATCH_REPLY -> Res.string.notice_msg_episode_patch_reply

        // 角色修订
        NoticeType.CHARACTER_PATCH_ACCEPTED -> Res.string.notice_msg_character_patch_accepted
        NoticeType.CHARACTER_PATCH_REJECTED -> Res.string.notice_msg_character_patch_rejected
        NoticeType.CHARACTER_PATCH_EXPIRED -> Res.string.notice_msg_character_patch_expired
        NoticeType.CHARACTER_PATCH_REPLY -> Res.string.notice_msg_character_patch_reply

        // 人物修订
        NoticeType.PERSON_PATCH_ACCEPTED -> Res.string.notice_msg_person_patch_accepted
        NoticeType.PERSON_PATCH_REJECTED -> Res.string.notice_msg_person_patch_rejected
        NoticeType.PERSON_PATCH_EXPIRED -> Res.string.notice_msg_person_patch_expired
        NoticeType.PERSON_PATCH_REPLY -> Res.string.notice_msg_person_patch_reply

        // 时间线的吐槽
        NoticeType.TIMELINE_SAY_REPLY -> Res.string.notice_msg_timeline_say_reply

        // 被提及
        NoticeType.SUBJECT_TOPIC_AT -> Res.string.notice_msg_subject_topic_reply_at
        NoticeType.BLOG_POST_AT -> Res.string.notice_msg_blog_reply_at
        NoticeType.GROUP_TOPIC_AT -> Res.string.notice_msg_group_topic_reply_at
        NoticeType.CHARACTER_POST_AT -> Res.string.notice_msg_character_reply_at
        NoticeType.PERSON_POST_AT -> Res.string.notice_msg_person_reply_at
        NoticeType.TIMELINE_SAY_AT -> Res.string.notice_msg_timeline_say_at
        NoticeType.EP_POST_AT -> Res.string.notice_msg_ep_reply_at

        else -> null
    }

    // 无 title 占位符的类型（好友类），直接返回纯字符串
    val rawText = if (template != null) stringResource(template, title) else title

    return remember(rawText, title, titleColor) {
        buildAnnotatedString {
            val titleStart = rawText.indexOf(title)
            if (title.isBlank() || titleStart <= 0) {
                append(rawText)
            } else {
                append(rawText.substring(0, titleStart))
                pushStyle(SpanStyle(color = titleColor, fontWeight = FontWeight.Medium))
                append(title)
                pop()
                append(rawText.substring(titleStart + title.length))
            }
        }
    }
}
