package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Class: [BlogDetailEntity]
 *
 * @author why
 * @since 11/30/23
 */
@Keep
@Parcelize
data class BlogDetailEntity(
    @SerializedName("id") var id: String = "",
    @SerializedName("title") var title: String = "",
    @SerializedName("time") var time: String = "",
    @SerializedName("userAvatar") var userAvatar: String = "",
    @SerializedName("userName") var userName: String = "",
    @SerializedName("userId") var userId: String = "",
    @SerializedName("content") var content: String = "",
    @SerializedName("anchorCommentId") var anchorCommentId: String = "",
    @SerializedName("related") var related: SampleRelatedEntity = SampleRelatedEntity(),
    @SerializedName("tags") var tags: List<MediaDetailEntity.MediaTag> = emptyList(),
    @SerializedName("comments") var comments: List<CommentTreeEntity> = emptyList(),
    @SerializedName("replyForm") var replyForm: CommentFormEntity = CommentFormEntity(),
) : Parcelable
