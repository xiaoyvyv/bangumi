package com.xiaoyv.common.api.parser.entity

/**
 * Class: [MediaReviewEntity]
 *
 * @author why
 * @since 11/29/23
 */
data class MediaReviewEntity(
    var id: String = "",
    var title:String="",
    var avatar: String = "",
    var userName: String = "",
    var userId: String = "",
    var time: String = "",
    var comment: String = "",
    var commentCount: Int = 0
)
