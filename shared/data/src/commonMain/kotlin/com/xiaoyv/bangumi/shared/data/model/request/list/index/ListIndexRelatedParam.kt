package com.xiaoyv.bangumi.shared.data.model.request.list.index

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.IndexCatType
import com.xiaoyv.bangumi.shared.core.types.IndexCatWebTabType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.data.model.ui.PageUI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [ListIndexRelatedParam]
 *
 * @author why
 * @since 2025/1/25
 */
@Immutable
@Serializable
data class ListIndexRelatedParam(
    @SerialName("ui") val ui: PageUI = PageUI(),

    @field:IndexCatWebTabType
    @SerialName("type")
    val type: String = IndexCatWebTabType.ALL,

    @SerialName("indexId")
    val indexId: Long = 0,
) {
    val uniqueKey: String = "$indexId-$type"

    /**
     * 目录内容的类型
     */
    val cat: Int?
        @IndexCatType
        get() = when (type) {
            IndexCatWebTabType.SUBJECT_ANIME,
            IndexCatWebTabType.SUBJECT_BOOK,
            IndexCatWebTabType.SUBJECT_MUSIC,
            IndexCatWebTabType.SUBJECT_GAME,
            IndexCatWebTabType.SUBJECT_REAL,
                -> IndexCatType.SUBJECT

            IndexCatWebTabType.CHARACTER -> IndexCatType.CHARACTER
            IndexCatWebTabType.PERSON -> IndexCatType.PERSON
            IndexCatWebTabType.EP -> IndexCatType.EP
            IndexCatWebTabType.BLOG -> IndexCatType.BLOG
            IndexCatWebTabType.TOPIC_GROUP -> IndexCatType.GROUP_TOPIC
            IndexCatWebTabType.TOPIC_SUBJECT -> IndexCatType.SUBJECT_TOPIC
            else -> null
        }

    /**
     * 目录内容为条目时，具体的子类型
     */
    val subjectType: Int?
        @SubjectType
        get() = when (type) {
            IndexCatWebTabType.SUBJECT_ANIME -> SubjectType.ANIME
            IndexCatWebTabType.SUBJECT_BOOK -> SubjectType.BOOK
            IndexCatWebTabType.SUBJECT_MUSIC -> SubjectType.MUSIC
            IndexCatWebTabType.SUBJECT_GAME -> SubjectType.GAME
            IndexCatWebTabType.SUBJECT_REAL -> SubjectType.REAL
            else -> null
        }

    companion object {
        val Empty = ListIndexRelatedParam()
    }
}