package com.xiaoyv.common.api.parser.entity

import com.xiaoyv.common.widget.image.AnimeGridImageView

/**
 * Class: [TimelineEntity]
 *
 * @author why
 * @since 11/25/23
 */
data class TimelineEntity(
    var avatar: String = "",
    var userId: String = "",
    var userActionText: String = "",
    var collectInfo: CollectionInfo? = null,
    var card: Card? = null,
    var character: Character? = null,
    var images: List<Image> = emptyList(),
    var timeText: String = ""
) {
    data class Character(
        var avatar: String,
        var subjectId: String,
        var userId: String
    )

    data class Image(
        var coverUrl: String = "",
        var subjectId: String = ""
    ) : AnimeGridImageView.Image {
        override val image: String get() = coverUrl
    }

    data class CollectionInfo(
        var score: String = "",
        var comment: String = ""
    )

    data class Info(
        var title: String,
    )

    data class Card(
        var title: String = "",
        var subjectId: String = "",
        var coverUrl: String = "",
        var cardTip: String = "",
        var cardRate: String = "",
        var cardRateTotal: String = "",
    )
}
