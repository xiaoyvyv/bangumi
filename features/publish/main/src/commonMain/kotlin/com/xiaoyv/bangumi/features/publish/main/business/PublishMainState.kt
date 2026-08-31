package com.xiaoyv.bangumi.features.publish.main.business

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue
import com.xiaoyv.bangumi.shared.core.types.PublishPostType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.StringResource

/**
 * [PublishMainState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class PublishMainState(
    @PublishPostType val type: Int = PublishPostType.TIMELINE_STATUS,
    val title: StringResource,
    val subject: TextFieldValue = TextFieldValue(""),
    val content: TextFieldValue = TextFieldValue(""),
    val turnstileToken: String = "",
    val turnstileRefreshKey: Long = 0,

    val attachSubjects: SerializeList<ComposeSubject> = persistentListOf(),
    val attachTags: SerializeList<String> = persistentListOf(),
    val public: Boolean = true,
) {
    val canPublish: Boolean
        get() = content.text.isNotBlank() && turnstileToken.isNotBlank() && (needsTitle.not() || subject.text.isNotBlank())

    /**
     * 需要标题的发布
     */
    val needsTitle: Boolean
        get() = type == PublishPostType.BLOG
                || type == PublishPostType.TOPIC_GROUP
                || type == PublishPostType.TOPIC_SUBJECT

    companion object {
        const val MAX_ATTACH_SUBJECT_COUNT = 5
    }
}
