package com.xiaoyv.common.api.parser.entity

/**
 * Class: [BlogDetailEntity]
 *
 * @author why
 * @since 11/30/23
 */
data class BlogDetailEntity(
    var id: String = "",
    var title: String = "",
    var time: String = "",
    var userAvatar: String = "",
    var userName: String = "",
    var userId: String = "",
    var content: String = "",
    var related: List<MediaDetailEntity.MediaRelative> = emptyList(),
    var tags: List<MediaDetailEntity.MediaTag> = emptyList(),
    var comments: List<CommentTreeEntity> = emptyList(),
    var replyForm: CommentFormEntity = CommentFormEntity(),
    var deleteHash: String = ""
)
