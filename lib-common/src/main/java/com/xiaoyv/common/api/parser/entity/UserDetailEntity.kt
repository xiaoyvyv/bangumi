package com.xiaoyv.common.api.parser.entity

/**
 * Class: [UserDetailEntity]
 *
 * @author why
 * @since 12/3/23
 */
data class UserDetailEntity(
    var id: String = "",
    var numberUid: String = "",
    var nickname: String = "",
    var avatar: String = "",
    var sign: String = "",
    var signPic: String = "",
    var lastOnlineTime: String = "",
    var createTime: String = "",
    var isFriend: Boolean = false,
    var gh: String = "",
    var chart: UserChart = UserChart(),
    var networkService: List<NetworkService> = emptyList(),
    var userSynchronize: Synchronize = Synchronize(),
    var anime: SaveOverview = SaveOverview(),
    var game: SaveOverview = SaveOverview(),
    var book: SaveOverview = SaveOverview(),
    var music: SaveOverview = SaveOverview(),
    var real: SaveOverview = SaveOverview(),
    var blog: List<MediaReviewBlogEntity> = emptyList(),
) {
    data class SaveOverview(
        var isEmpty: Boolean = true,
        var title: String = "",
        var count: List<String> = emptyList(),
        var doing: List<MediaDetailEntity.MediaRelative> = emptyList(),
        var collect: List<MediaDetailEntity.MediaRelative> = emptyList(),
    )

    data class Synchronize(
        var rate: String = "0%",
        var syncCount: Int = 0,
    )

    data class NetworkService(
        var title: String = "",
        var background: String = "",
        var tip: String = "",
    )

    data class UserChart(
        var saveCount: Int = 0,
        var finishCount: Int = 0,
        var finishRate: Float = 0f,
        var averageScore: Float = 0f,
        var standardDeviation: Float = 0f,
        var ratingCount: Int = 0,
        var ratingDetail: List<MediaDetailEntity.RatingItem> = emptyList(),
    )
}
