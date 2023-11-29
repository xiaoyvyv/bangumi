package com.xiaoyv.common.api.parser.entity

/**
 * Class: [MediaCharacterEntity]
 *
 * @author why
 * @since 11/29/23
 */
data class MediaCharacterEntity(
    var id: String = "",
    var avatar: String = "",
    var titleCn: String = "",
    var titleNative: String = "",
    var personJob: String = "",
    var personSex: String = "",
    var commentCount: String = "",
    var actors: List<ActorBadge> = emptyList(),
) {
    data class ActorBadge(
        var name: String = "",
        var nameCn: String = "",
        var avatar: String = "",
        var id: String = ""
    )
}
