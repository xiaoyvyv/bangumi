package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
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
    var id: String = "",
    var floor: String = "#1",
    var time: String = "",
    var replyJs: String = "",
    var userAvatar: String = "",
    var userName: String = "",
    var userId: String = "",
    var replyContent: String = "",
    var topicSubReply: List<CommentTreeEntity> = emptyList()
) : Parcelable
