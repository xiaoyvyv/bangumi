package com.xiaoyv.common.api.parser.entity

/**
 * Class: [MediaChapterEntity]
 *
 * @author why
 * @since 11/29/23
 */
data class MediaChapterEntity(
    var id: String = "",
    var titleCn: String = "",
    var titleNative: String = "",
    var finished: Boolean = false,
    var time: String = "",
    var comment: String = ""
)
