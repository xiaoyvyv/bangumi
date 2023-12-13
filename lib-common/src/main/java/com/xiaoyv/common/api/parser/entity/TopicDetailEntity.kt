package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * Class: [TopicDetailEntity]
 *
 * @author why
 * @since 12/2/23
 */

@Keep
@Parcelize
data class TopicDetailEntity(
    @SerializedName("id") var id: String = "",
    @SerializedName("title") var title: String = "",
    @SerializedName("time") var time: String = "",
    @SerializedName("userAvatar") var userAvatar: String = "",
    @SerializedName("userName") var userName: String = "",
    @SerializedName("userId") var userId: String = "",
    @SerializedName("userSign") var userSign: String = "",
    @SerializedName("content") var content: String = "",
    @SerializedName("related") var related: SampleRelatedEntity = SampleRelatedEntity(),
    @SerializedName("comments") var comments: List<CommentTreeEntity> = emptyList(),
    @SerializedName("replyForm") var replyForm: CommentFormEntity = CommentFormEntity(),
    @SerializedName("deleteHash") var deleteHash: String = "",
    @SerializedName("likeMap") var likeMap: LikeEntity? = null,
    @SerializedName("likeEmojis") var likeEmojis: List<LikeEmoji> = emptyList()
) : Parcelable {

    @Keep
    @Parcelize
    data class LikeEmoji(
        @SerializedName("likeValue") var likeValue: String = "",
        @SerializedName("emojiUrl") var emojiUrl: String = ""
    ) : Parcelable
}