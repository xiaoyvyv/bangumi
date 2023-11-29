package com.xiaoyv.common.api.parser.entity

/**
 * Class: [MediaDetailEntity]
 *
 * @author why
 * @since 11/29/23
 */
data class MediaDetailEntity(
    var id: String = "",
    var titleCn: String = "",
    var titleNative: String = "",
    var cover: String = "",
    var infos: List<String> = emptyList(),
    var recommendIndex: List<MediaIndex> = emptyList(),
    var whoSee: List<MediaWhoSee> = emptyList(),
    var countWish: Int = 0,
    var countDoing: Int = 0,
    var countOnHold: Int = 0,
    var countCollect: Int = 0,
    var countDropped: Int = 0,
    var progressList: List<MediaProgress> = emptyList(),
    var subjectSummary: String = "",
    var tags: List<MediaTag> = emptyList(),
    var characters: List<MediaCharacter> = emptyList(),
    var relativeMedia: List<MediaRelative> = emptyList(),
    var sameLikes: List<MediaRelative> = emptyList(),
    var reviews: List<MediaReviewEntity> = emptyList(),
    var boards: List<MediaBoardEntity> = emptyList(),
    var comments: List<MediaCommentEntity> = emptyList(),
    var rating: MediaRating = MediaRating(),
) {
    data class MediaRating(
        var globalRating: Float? = null,
        var globalRank: Int = 0,
        var ratingCount: Int = 0,
        var ratingDetail: List<RatingItem> = emptyList()
    )

    data class RatingItem(
        var label: String = "",
        var count: Int = 0,
        var percent: Float = 0f
    )

    data class MediaRelative(
        var cover: String = "",
        var id: String = "",
        var titleCn: String = "",
        var titleNative: String = "",
        var type: String = ""
    )

    data class MediaCharacter(
        var avatar: String = "",
        var id: String = "",
        var characterName: String = "",
        var characterNameCn: String = "",
        var saveCount: Int = 0,
        var job: String = "",
        var persons: List<MediaCharacterPerson> = emptyList()
    )

    data class MediaCharacterPerson(
        var personId: String = "",
        var personName: String = "",
    )


    data class MediaTag(
        var tagName: String = "",
        var title: String = "",
        var count: Int = 0,
    )

    data class MediaProgress(
        var titleNative: String = "",
        var titleCn: String = "",
        var firstTime: String = "",
        var duration: String = "",
        var id: String = "",
        var no: String = "",
        var commentCount: Int = 0
    )

    data class MediaIndex(
        var userName: String = "",
        var userId: String = "",
        var userAvatar: String = "",
        var id: String = "",
        var title: String = ""
    )

    data class MediaWhoSee(
        var userName: String = "",
        var userId: String = "",
        var userAvatar: String = "",
        var time: String = "",
        var star: Float = 0f
    )
}
