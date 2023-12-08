package com.xiaoyv.common.api.parser.entity

/**
 * Class: [SuperTopicEntity]
 *
 * @author why
 * @since 11/26/23
 */
data class SuperTopicEntity(
    var userName: String = "",
    var userId: String = "",
    var avatarUrl: String = "",
    var title: String = "",
    var titleLink: String = "",
    var attachLink: String = "",
    var attachTitle: String = "",
    var time: String = "",
    var commentCount: Int = 0,
)
