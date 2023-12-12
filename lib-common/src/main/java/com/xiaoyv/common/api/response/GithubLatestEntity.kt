package com.xiaoyv.common.api.response

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Class: [GithubLatestEntity]
 *
 * @author why
 * @since 12/12/23
 */
@Keep
@Parcelize
data class GithubLatestEntity(
    @SerializedName("assets")
    var assets: List<Asset>? = null,
    @SerializedName("assets_url")
    var assetsUrl: String? = null,
    @SerializedName("author")
    var author: Author? = null,
    @SerializedName("body")
    var body: String? = null,
    @SerializedName("created_at")
    var createdAt: String? = null,
    @SerializedName("draft")
    var draft: Boolean = false,
    @SerializedName("html_url")
    var htmlUrl: String? = null,
    @SerializedName("id")
    var id: Long = 0,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("node_id")
    var nodeId: String? = null,
    @SerializedName("prerelease")
    var prerelease: Boolean = false,
    @SerializedName("published_at")
    var publishedAt: String? = null,
    @SerializedName("reactions")
    var reactions: Reactions? = null,
    @SerializedName("tag_name")
    var tagName: String? = null,
    @SerializedName("tarball_url")
    var tarballUrl: String? = null,
    @SerializedName("target_commitish")
    var targetCommitish: String? = null,
    @SerializedName("upload_url")
    var uploadUrl: String? = null,
    @SerializedName("url")
    var url: String? = null,
    @SerializedName("zipball_url")
    var zipballUrl: String? = null
) : Parcelable {

    @Keep
    @Parcelize
    data class Asset(
        @SerializedName("browser_download_url")
        var browserDownloadUrl: String? = null,
        @SerializedName("content_type")
        var contentType: String? = null,
        @SerializedName("created_at")
        var createdAt: String? = null,
        @SerializedName("download_count")
        var downloadCount: Int = 0,
        @SerializedName("id")
        var id: Long = 0,
        @SerializedName("label")
        var label: String? = null,
        @SerializedName("name")
        var name: String? = null,
        @SerializedName("node_id")
        var nodeId: String? = null,
        @SerializedName("size")
        var size: Int = 0,
        @SerializedName("state")
        var state: String? = null,
        @SerializedName("updated_at")
        var updatedAt: String? = null,
        @SerializedName("uploader")
        var uploader: Uploader? = null,
        @SerializedName("url")
        var url: String? = null
    ) : Parcelable

    @Keep
    @Parcelize
    data class Author(
        @SerializedName("avatar_url")
        var avatarUrl: String? = null,
        @SerializedName("events_url")
        var eventsUrl: String? = null,
        @SerializedName("followers_url")
        var followersUrl: String? = null,
        @SerializedName("following_url")
        var followingUrl: String? = null,
        @SerializedName("gists_url")
        var gistsUrl: String? = null,
        @SerializedName("gravatar_id")
        var gravatarId: String? = null,
        @SerializedName("html_url")
        var htmlUrl: String? = null,
        @SerializedName("id")
        var id: Long = 0,
        @SerializedName("login")
        var login: String? = null,
        @SerializedName("node_id")
        var nodeId: String? = null,
        @SerializedName("organizations_url")
        var organizationsUrl: String? = null,
        @SerializedName("received_events_url")
        var receivedEventsUrl: String? = null,
        @SerializedName("repos_url")
        var reposUrl: String? = null,
        @SerializedName("site_admin")
        var siteAdmin: Boolean = false,
        @SerializedName("starred_url")
        var starredUrl: String? = null,
        @SerializedName("subscriptions_url")
        var subscriptionsUrl: String? = null,
        @SerializedName("type")
        var type: String? = null,
        @SerializedName("url")
        var url: String? = null
    ) : Parcelable

    @Keep
    @Parcelize
    data class Reactions(
        @SerializedName("confused")
        var confused: Int = 0,
        @SerializedName("eyes")
        var eyes: Int = 0,
        @SerializedName("heart")
        var heart: Int = 0,
        @SerializedName("hooray")
        var hooray: Int = 0,
        @SerializedName("laugh")
        var laugh: Int = 0,
        @SerializedName("rocket")
        var rocket: Int = 0,
        @SerializedName("total_count")
        var totalCount: Int = 0,
        @SerializedName("url")
        var url: String? = null
    ) : Parcelable

    @Keep
    @Parcelize
    data class Uploader(
        @SerializedName("avatar_url")
        var avatarUrl: String? = null,
        @SerializedName("events_url")
        var eventsUrl: String? = null,
        @SerializedName("followers_url")
        var followersUrl: String? = null,
        @SerializedName("following_url")
        var followingUrl: String? = null,
        @SerializedName("gists_url")
        var gistsUrl: String? = null,
        @SerializedName("gravatar_id")
        var gravatarId: String? = null,
        @SerializedName("html_url")
        var htmlUrl: String? = null,
        @SerializedName("id")
        var id: Long = 0,
        @SerializedName("login")
        var login: String? = null,
        @SerializedName("node_id")
        var nodeId: String? = null,
        @SerializedName("organizations_url")
        var organizationsUrl: String? = null,
        @SerializedName("received_events_url")
        var receivedEventsUrl: String? = null,
        @SerializedName("repos_url")
        var reposUrl: String? = null,
        @SerializedName("site_admin")
        var siteAdmin: Boolean = false,
        @SerializedName("starred_url")
        var starredUrl: String? = null,
        @SerializedName("subscriptions_url")
        var subscriptionsUrl: String? = null,
        @SerializedName("type")
        var type: String? = null,
        @SerializedName("url")
        var url: String? = null
    ) : Parcelable
}