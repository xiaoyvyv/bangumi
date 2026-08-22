package com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposePixivBookmarkAddResponse(
    @SerialName("last_bookmark_id") val lastBookmarkId: Long = 0,
    @SerialName("stacc_status_id") val staccStatusId: Long = 0
) {
    companion object {
        val Empty = ComposePixivBookmarkAddResponse()
    }
}

@Immutable
@Serializable
data class ComposePixivBookmarkTag(
    @SerialName("tag") val tag: String = "",
    @SerialName("cnt") val cnt: Int = 0
) {
    companion object {
        val Empty = ComposePixivBookmarkTag()
    }
}

@Immutable
@Serializable
data class ComposePixivBookmarkTagsResponse(
    @SerialName("public") val public: SerializeList<ComposePixivBookmarkTag> = persistentListOf(),
    @SerialName("private") val private: SerializeList<ComposePixivBookmarkTag> = persistentListOf(),
    @SerialName("tooManyBookmark") val tooManyBookmark: Boolean = false,
    @SerialName("tooManyBookmarkTags") val tooManyBookmarkTags: Boolean = false
) {
    companion object {
        val Empty = ComposePixivBookmarkTagsResponse()
    }
}

@Immutable
@Serializable
data class ComposePixivUserBookmarkIllustsBody(
    @SerialName("works") val works: SerializeList<ComposePixivIllustSimple> = persistentListOf(),
    @SerialName("total") val total: Int = 0,
    @SerialName("zoneConfig") val zoneConfig: ComposePixivZoneConfig = ComposePixivZoneConfig.Empty,
    @SerialName("extraData") val extraData: ComposePixivExtraData = ComposePixivExtraData.Empty
) {
    companion object {
        val Empty = ComposePixivUserBookmarkIllustsBody()
    }
}

@Immutable
@Serializable
data class ComposePixivUserBookmarkNovelsBody(
    @SerialName("works") val works: SerializeList<ComposePixivNovelSimple> = persistentListOf(),
    @SerialName("total") val total: Int = 0,
    @SerialName("zoneConfig") val zoneConfig: ComposePixivZoneConfig = ComposePixivZoneConfig.Empty,
    @SerialName("extraData") val extraData: ComposePixivExtraData = ComposePixivExtraData.Empty
) {
    companion object {
        val Empty = ComposePixivUserBookmarkNovelsBody()
    }
}
