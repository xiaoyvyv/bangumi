package com.xiaoyv.bangumi.ui.media.detail.score.page

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.MediaScoreEntity
import com.xiaoyv.common.api.parser.impl.parserMediaScore
import com.xiaoyv.common.config.annotation.InterestType

/**
 * Class: [MediaScorePageViewModel]
 *
 * @author why
 * @since 1/22/24
 */
class MediaScorePageViewModel : BaseListViewModel<MediaScoreEntity>() {
    internal var forFriend: Boolean = false
    internal var mediaId: String = ""

    @InterestType
    internal var interestType: String = InterestType.TYPE_UNKNOWN

    override suspend fun onRequestListImpl(): List<MediaScoreEntity> {
        return BgmApiManager.bgmWebApi.queryMediaScore(
            mediaId = mediaId,
            collectType = collectType(),
            filter = if (forFriend) "friends" else null,
            page = current
        ).parserMediaScore()
    }

    private fun collectType(): String {
        return when (interestType) {
            InterestType.TYPE_WISH -> "wishes"
            InterestType.TYPE_COLLECT -> "collections"
            InterestType.TYPE_DO -> "doings"
            InterestType.TYPE_ON_HOLD -> "on_hold"
            InterestType.TYPE_DROPPED -> "dropped"
            else -> ""
        }
    }
}