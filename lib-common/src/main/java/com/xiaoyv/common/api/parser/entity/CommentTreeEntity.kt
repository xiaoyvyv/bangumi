package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.xiaoyv.common.api.request.EmojiParam
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [CommentTreeEntity]
 *
 * @author why
 * @since 12/1/23
 */
@Keep
@Parcelize
data class CommentTreeEntity(
    @SerializedName("id") override var id: String = "",
    @SerializedName("floor") var floor: String = "#1",
    @SerializedName("time") var time: String = "",
    @SerializedName("replyJs") var replyJs: String = "",
    @SerializedName("userAvatar") var userAvatar: String = "",
    @SerializedName("userName") var userName: String = "",
    @SerializedName("userId") var userId: String = "",
    @SerializedName("userSign") var userSign: String = "",
    @SerializedName("replyContent") var replyContent: String = "",
    @SerializedName("topicSubReply") var topicSubReply: List<CommentTreeEntity> = emptyList(),
    @SerializedName("replyQuote") var replyQuote: String = "",
    @SerializedName("emojiParam") var emojiParam: EmojiParam = EmojiParam(),
    @SerializedName("emojis") var emojis: List<LikeEntity.LikeAction> = emptyList(),
    @SerializedName("anchored") var anchored: Boolean = false,
) : Parcelable, IdEntity {


}
