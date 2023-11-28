package com.xiaoyv.common.api.parser.entity

/**
 * Class: [MediaCommentEntity]
 *
 * @author why
 * @since 11/29/23
 */
data class MediaCommentEntity(
    var id: String = "",
    var avatar: String = "",
    var userName: String = "",
    var userId: String = "",
    var time: String = "",
    var comment: String = "",
    var star: Float = 0f
)
