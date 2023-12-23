package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.xiaoyv.common.api.request.EmojiParam
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
    @SerializedName("mineId") var mineId: String = "",
    @SerializedName("userSign") var userSign: String = "",
    @SerializedName("content") var content: String = "",
    @SerializedName("related") var related: SampleRelatedEntity = SampleRelatedEntity(),
    @SerializedName("comments") var comments: List<CommentTreeEntity> = emptyList(),
    @SerializedName("replyForm") var replyForm: CommentFormEntity = CommentFormEntity(),
    @SerializedName("emojiParam") var emojiParam: EmojiParam = EmojiParam(),
    @SerializedName("emojis") var emojis: List<LikeEntity.LikeAction> = emptyList(),
) : Parcelable {

    /**
     * 填充 like 信息
     */
    fun fillLikeInfo(totalLikeEmojiMap: Map<String, List<LikeEntity.LikeAction>>) {
        // 填充主题的贴贴
        if (emojiParam.enable) {
            emojis = totalLikeEmojiMap[emojiParam.likeCommentId].orEmpty()
        }

        // 填充评论的贴贴
        comments.onEach { main ->
            main.emojis = totalLikeEmojiMap[main.id].orEmpty()
            main.topicSubReply.onEach { sub ->
                sub.emojis = totalLikeEmojiMap[sub.id].orEmpty()
            }
        }
    }
}