package com.xiaoyv.common.api.parser.entity

/**
 * Class: [MediaMakerEntity]
 *
 * @author why
 * @since 11/29/23
 */
data class MediaMakerEntity(
    var id: String = "",
    var avatar: String = "",
    var titleCn: String = "",
    var titleNative: String = "",
    var personInfo: List<String> = emptyList(),
    var commentCount: String = "",
    var tip: String = ""
)
