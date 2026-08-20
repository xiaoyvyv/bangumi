package com.xiaoyv.bangumi.shared.data.model.response.bgm.user

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeUserPrivacy(
    @SerialName("settings") val settings: ComposeUserPrivacySettings = ComposeUserPrivacySettings.Empty,
    @SerialName("preferences") val preferences: ComposeUserPrivacyPreferences = ComposeUserPrivacyPreferences.Empty
) {

    @Immutable
    @Serializable
    data class ComposeUserPrivacyPreferences(
        @SerialName("showNsfwSubject") val showNsfwSubject: Boolean = false,
        @SerialName("canSetNsfwSubject") val canSetNsfwSubject: Boolean = true,
        @SerialName("allowNsfw") val allowNsfw: Boolean = false
    ) {
        companion object {
            val Empty = ComposeUserPrivacyPreferences()
        }
    }

    @Immutable
    @Serializable
    data class ComposeUserPrivacySettings(
        @SerialName("privateMessage") val privateMessage: PrivacyPrivateMessage = PrivacyPrivateMessage.ALL,
        @SerialName("timelineReply") val timelineReply: PrivacyTimelineReply = PrivacyTimelineReply.ALL,
        @SerialName("timelineCollectReply") val timelineCollectReply: PrivacyTimelineCollectReply = PrivacyTimelineCollectReply.ALL,
        @SerialName("follow") val follow: PrivacyFollow = PrivacyFollow.ALL,
        @SerialName("mentionNotification") val mentionNotification: PrivacyMentionNotification = PrivacyMentionNotification.ALL,
        @SerialName("commentNotification") val commentNotification: PrivacyCommentNotification = PrivacyCommentNotification.ALL,
        @SerialName("friendNotification") val friendNotification: PrivacyFriendNotification = PrivacyFriendNotification.ALL
    ) {
        companion object {
            val Empty = ComposeUserPrivacySettings()
        }

        /**
         * Values: all,friends,none
         */
        @Immutable
        @Serializable
        enum class PrivacyPrivateMessage(val value: String) {
            @SerialName("all")
            ALL("all"),
            @SerialName("friends")
            FRIENDS("friends"),
            @SerialName("none")
            NONE("none");
        }

        /**
         * Values: all,friends,none
         */
        @Serializable
        enum class PrivacyTimelineReply(val value: String) {
            @SerialName("all")
            ALL("all"),
            @SerialName("friends")
            FRIENDS("friends"),
            @SerialName("none")
            NONE("none");
        }

        /**
         * Values: all,friends,none
         */
        @Serializable
        enum class PrivacyTimelineCollectReply(val value: String) {
            @SerialName("all")
            ALL("all"),
            @SerialName("friends")
            FRIENDS("friends"),
            @SerialName("none")
            NONE("none");
        }

        /**
         * Values: all,none
         */
        @Serializable
        enum class PrivacyFollow(val value: String) {
            @SerialName("all")
            ALL("all"),
            @SerialName("none")
            NONE("none");
        }

        /**
         * Values: all,friends,none
         */
        @Serializable
        enum class PrivacyMentionNotification(val value: String) {
            @SerialName("all")
            ALL("all"),
            @SerialName("friends")
            FRIENDS("friends"),
            @SerialName("none")
            NONE("none");
        }

        /**
         * Values: all,friends,none
         */
        @Serializable
        enum class PrivacyCommentNotification(val value: String) {
            @SerialName("all")
            ALL("all"),
            @SerialName("friends")
            FRIENDS("friends"),
            @SerialName("none")
            NONE("none");
        }

        /**
         * Values: all,none
         */
        @Serializable
        enum class PrivacyFriendNotification(val value: String) {
            @SerialName("all")
            ALL("all"),
            @SerialName("none")
            NONE("none");
        }
    }

    companion object {
        val Empty = ComposeUserPrivacy()
    }
}