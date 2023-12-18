package com.xiaoyv.common.api.parser.entity

/**
 * Class: [BlogEntity]
 *
 * @author why
 * @since 11/28/23
 */
data class BlogEntity(
    var id: String = "",
    var title: String = "",
    var image: String = "",
    var time: String = "",
    var content: String = "",
    var commentCount: Int = 0,
    var mediaName: String = "",
    var recentUserName: String = "",
    var recentUserId: String = "",
    var nestingProfile: Boolean = false,
)
