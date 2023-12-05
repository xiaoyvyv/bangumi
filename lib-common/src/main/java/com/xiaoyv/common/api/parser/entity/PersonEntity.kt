package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.xiaoyv.common.config.annotation.MediaType
import kotlinx.parcelize.Parcelize

/**
 * Class: [PersonEntity]
 *
 * @author why
 * @since 12/4/23
 */
@Keep
@Parcelize
data class PersonEntity(
    @SerializedName("id") var id: String = "",
    @SerializedName("isVirtual") var isVirtual: Boolean = false,
    @SerializedName("nameCn") var nameCn: String = "",
    @SerializedName("nameNative") var nameNative: String = "",
    @SerializedName("poster") var poster: String = "",
    @SerializedName("posterLarge") var posterLarge: String = "",
    @SerializedName("job") var job: String = "",
    @SerializedName("isCollected") var isCollected: Boolean = false,
    @SerializedName("gh") var gh: String = "",
    @SerializedName("summary") var summary: CharSequence = "",
    @SerializedName("infos") var infos: List<SubInfo> = emptyList(),
    @SerializedName("recommendIndexes") var recommendIndexes: List<MediaDetailEntity.MediaIndex> = emptyList(),
    @SerializedName("whoCollects") var whoCollects: List<MediaDetailEntity.MediaWho> = emptyList(),
    @SerializedName("performers") var performers: List<RecentlyCharacter> = emptyList(),
    @SerializedName("recentCharacters") var recentCharacters: List<RecentlyCharacter> = emptyList(),
    @SerializedName("recentOpuses") var recentOpuses: List<RecentlyOpus> = emptyList(),
    @SerializedName("recentCooperates") var recentCooperates: List<RecentCooperate> = emptyList(),
    @SerializedName("comments") var comments: List<CommentTreeEntity> = emptyList(),
) : Parcelable {

    @Keep
    @Parcelize
    data class SubInfo(var title: String = "", var value: String = "") : Parcelable

    @Keep
    @Parcelize
    data class RecentCooperate(
        @SerializedName("id") var id: String = "",
        @SerializedName("avatar") var avatar: String = "",
        @SerializedName("name") var name: String = "",
        @SerializedName("times") var times: Int = 0,
        var jobs: List<String> = emptyList(),
        var infos: List<Pair<String, String>> = emptyList(),
        var opus: List<MediaDetailEntity.MediaRelative> = emptyList(),
    ) : Parcelable

    @Keep
    @Parcelize
    data class RecentlyOpus(
        @SerializedName("cover") var cover: String = "",
        @SerializedName("title") var title: String = "",
        @SerializedName("id") var id: String = "",
        @SerializedName("job") var job: String = "",
        @SerializedName("mediaType") @MediaType var mediaType: String = MediaType.TYPE_UNKNOWN,
    ) : Parcelable

    @Keep
    @Parcelize
    data class RecentlyCharacter(
        @SerializedName("character") var character: MediaDetailEntity.MediaCharacter = MediaDetailEntity.MediaCharacter(),
        @SerializedName("media") var media: MediaDetailEntity.MediaRelative = MediaDetailEntity.MediaRelative()
    ) : Parcelable
}
