package com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposePixivBackground(
    @SerialName("repeat") val repeat: String = "",
    @SerialName("color") val color: String = "",
    @SerialName("url") val url: String = "",
    @SerialName("isPrivate") val isPrivate: Boolean = false
) {
    companion object {
        val Empty = ComposePixivBackground()
    }
}

@Immutable
@Serializable
data class ComposePixivUserRegion(
    @SerialName("name") val name: String = "",
    @SerialName("region") val region: String = "",
    @SerialName("prefecture") val prefecture: String = "",
    @SerialName("privacyLevel") val privacyLevel: String = ""
) {
    companion object {
        val Empty = ComposePixivUserRegion()
    }
}

@Immutable
@Serializable
data class ComposePixivUserInfoBody(
    @SerialName("userId") val userId: Long = 0,
    @SerialName("name") val name: String = "",
    @SerialName("image") val image: String = "",
    @SerialName("imageBig") val imageBig: String = "",
    @SerialName("premium") val premium: Boolean = false,
    @SerialName("isFollowed") val isFollowed: Boolean = false,
    @SerialName("isMypixiv") val isMypixiv: Boolean = false,
    @SerialName("isBlocking") val isBlocking: Boolean = false,
    @SerialName("background") val background: ComposePixivBackground = ComposePixivBackground.Empty,
    @SerialName("sketchLiveId") val sketchLiveId: Long = 0,
    @SerialName("partial") val partial: Int = 0,
    @SerialName("following") val following: Int = 0,
    @SerialName("mypixivCount") val mypixivCount: Int = 0,
    @SerialName("followedBack") val followedBack: Boolean = false,
    @SerialName("comment") val comment: String = "",
    @SerialName("commentHtml") val commentHtml: String = "",
    @SerialName("webpage") val webpage: String = "",
    @SerialName("canSendMessage") val canSendMessage: Boolean = false,
    @SerialName("region") val region: ComposePixivUserRegion = ComposePixivUserRegion.Empty,
    @SerialName("official") val official: Boolean = false
) {
    companion object {
        val Empty = ComposePixivUserInfoBody()
    }
}

@Immutable
@Serializable
data class ComposePixivFollowingUser(
    @SerialName("userId") val userId: Long = 0,
    @SerialName("userName") val userName: String = "",
    @SerialName("profileImageUrl") val profileImageUrl: String = "",
    @SerialName("profileImageSmallUrl") val profileImageSmallUrl: String = "",
    @SerialName("userComment") val userComment: String = "",
    @SerialName("premium") val premium: Boolean = false,
    @SerialName("following") val following: Boolean = false,
    @SerialName("followed") val followed: Boolean = false,
    @SerialName("isBlocking") val isBlocking: Boolean = false,
    @SerialName("isMypixiv") val isMypixiv: Boolean = false,
    @SerialName("illusts") val illusts: SerializeList<ComposePixivIllustSimple> = persistentListOf(),
    @SerialName("novels") val novels: SerializeList<ComposePixivNovelSimple> = persistentListOf()
) {
    companion object {
        val Empty = ComposePixivFollowingUser()
    }
}

@Immutable
@Serializable
data class ComposePixivUserFollowingBody(
    @SerialName("users") val users: SerializeList<ComposePixivFollowingUser> = persistentListOf(),
    @SerialName("total") val total: Int = 0,
    @SerialName("followUserTags") val followUserTags: SerializeList<String> = persistentListOf(),
    @SerialName("zoneConfig") val zoneConfig: ComposePixivZoneConfig = ComposePixivZoneConfig.Empty,
    @SerialName("extraData") val extraData: ComposePixivExtraData = ComposePixivExtraData.Empty
) {
    companion object {
        val Empty = ComposePixivUserFollowingBody()
    }
}

@Immutable
@Serializable
data class ComposePixivUnfollowUserResponse(
    @SerialName("user_id") val userId: Long = 0,
    @SerialName("type") val type: String = ""
) {
    companion object {
        val Empty = ComposePixivUnfollowUserResponse()
    }
}

@Immutable
@Serializable
data class ComposePixivProfileAllBody(
    @SerialName("illusts") val illusts: SerializeMap<String, ComposePixivIllustSimple> = persistentMapOf(),
    @SerialName("manga") val manga: SerializeMap<String, ComposePixivIllustSimple> = persistentMapOf(),
    @SerialName("novels") val novels: SerializeMap<String, ComposePixivNovelSimple> = persistentMapOf(),
    @SerialName("pickup") val pickup: SerializeList<ComposePixivIllustSimple> = persistentListOf()
) {
    companion object {
        val Empty = ComposePixivProfileAllBody()
    }
}

@Immutable
@Serializable
data class ComposePixivProfileIllustsBody(
    @SerialName("works") val works: SerializeMap<String, ComposePixivIllustSimple> = persistentMapOf()
) {
    companion object {
        val Empty = ComposePixivProfileIllustsBody()
    }
}

@Immutable
@Serializable
data class ComposePixivProfileNovelsBody(
    @SerialName("works") val works: SerializeMap<String, ComposePixivNovelSimple> = persistentMapOf()
) {
    companion object {
        val Empty = ComposePixivProfileNovelsBody()
    }
}

@Immutable
@Serializable
data class ComposePixivUserRecommendBody(
    @SerialName("users") val users: SerializeList<ComposePixivFollowingUser> = persistentListOf()
) {
    companion object {
        val Empty = ComposePixivUserRecommendBody()
    }
}

@Immutable
@Serializable
data class ComposePixivDiscoveryUsersBody(
    @SerialName("users") val users: SerializeList<ComposePixivFollowingUser> = persistentListOf()
) {
    companion object {
        val Empty = ComposePixivDiscoveryUsersBody()
    }
}

@Immutable
@Serializable
data class ComposePixivUserFollowDetailBody(
    @SerialName("restrict") val restrict: String = "",
    @SerialName("tag") val tag: String = ""
) {
    companion object {
        val Empty = ComposePixivUserFollowDetailBody()
    }
}

@Immutable
@Serializable
data class ComposePixivMyPixivBody(
    @SerialName("users") val users: SerializeList<ComposePixivFollowingUser> = persistentListOf()
) {
    companion object {
        val Empty = ComposePixivMyPixivBody()
    }
}

@Immutable
@Serializable
data class ComposePixivUserIllustsByTagBody(
    @SerialName("works") val works: SerializeList<ComposePixivIllustSimple> = persistentListOf(),
    @SerialName("total") val total: Int = 0
) {
    companion object {
        val Empty = ComposePixivUserIllustsByTagBody()
    }
}

@Immutable
@Serializable
data class ComposePixivUserNovelsByTagBody(
    @SerialName("works") val works: SerializeList<ComposePixivNovelSimple> = persistentListOf(),
    @SerialName("total") val total: Int = 0
) {
    companion object {
        val Empty = ComposePixivUserNovelsByTagBody()
    }
}

@Immutable
@Serializable
data class ComposePixivUserIllustTag(
    @SerialName("tag") val tag: String = "",
    @SerialName("cnt") val cnt: Int = 0,
    @SerialName("translation") val translation: SerializeMap<String, String> = persistentMapOf()
) {
    companion object {
        val Empty = ComposePixivUserIllustTag()
    }
}

@Immutable
@Serializable
data class ComposePixivUserSearchBody(
    @SerialName("users") val users: SerializeList<ComposePixivFollowingUser> = persistentListOf(),
    @SerialName("total") val total: Int = 0
) {
    companion object {
        val Empty = ComposePixivUserSearchBody()
    }
}
