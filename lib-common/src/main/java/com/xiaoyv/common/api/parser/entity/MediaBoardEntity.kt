package com.xiaoyv.common.api.parser.entity

/**
 * Class: [MediaBoardEntity]
 *
 * @author why
 * @since 11/29/23
 */
data class MediaBoardEntity(
    var id: String = "",
    var content: String = "",
    var userName: String = "",
    var userId: String = "",
    var time: String = "",
    var replies: String = "",
)
