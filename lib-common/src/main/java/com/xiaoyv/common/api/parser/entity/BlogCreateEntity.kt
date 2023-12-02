package com.xiaoyv.common.api.parser.entity

/**
 * Class: [BlogCreateEntity]
 *
 * @author why
 * @since 12/2/23
 */
data class BlogCreateEntity(
    var formHash: String = "",
    var related: List<MediaDetailEntity.MediaRelative> = emptyList(),
    var pageName: String = "",
    var userName: String = "",
    var time: String = "",
    var title: String = "",
    var content: String = "",
    var tags: String = "",
    var isPublic: Boolean = true,
)