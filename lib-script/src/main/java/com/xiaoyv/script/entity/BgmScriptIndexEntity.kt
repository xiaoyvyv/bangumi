package com.xiaoyv.script.entity

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * Class: [BgmScriptIndexEntity]
 *
 * @author why
 * @since 1/15/24
 */
data class BgmScriptIndexEntity(
    @SerializedName("ban")
    var ban: Boolean = false,
    @SerializedName("created_at")
    var createdAt: Date? = null,
    @SerializedName("creator")
    var creator: Creator? = null,
    @SerializedName("desc")
    var desc: String? = null,
    @SerializedName("id")
    var id: Long = 0,
    @SerializedName("nsfw")
    var nsfw: Boolean = false,
    @SerializedName("stat")
    var stat: Stat? = null,
    @SerializedName("title")
    var title: String? = null,
    @SerializedName("total")
    var total: Int = 0,
    @SerializedName("updated_at")
    var updatedAt: Date? = null,
) {
    data class Creator(
        @SerializedName("nickname")
        var nickname: String? = null,
        @SerializedName("username")
        var username: String? = null,
    )

    data class Stat(
        @SerializedName("collects")
        var collects: Int = 0,
        @SerializedName("comments")
        var comments: Int = 0,
    )
}