package com.xiaoyv.bangumi.ui.media.detail.chapter

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.MediaChapterEntity
import com.xiaoyv.common.api.parser.impl.parserMediaChapters
import com.xiaoyv.common.api.response.api.ApiEpisodeEntity
import com.xiaoyv.common.api.response.api.ApiUserEpEntity
import com.xiaoyv.common.config.annotation.EpApiType
import com.xiaoyv.common.config.annotation.EpCollectType
import com.xiaoyv.common.config.annotation.MediaDetailType
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.helper.UserHelper

/**
 * Class: [MediaChapterViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class MediaChapterViewModel : BaseListViewModel<MediaChapterEntity>() {
    /**
     * 媒体ID
     */
    internal var mediaId: String = ""

    override suspend fun onRequestListImpl(): List<MediaChapterEntity> {
        require(mediaId.isNotBlank()) { "媒体ID不存在" }

        if (ConfigHelper.disableApi) {
            return BgmApiManager.bgmWebApi.queryMediaDetail(mediaId, MediaDetailType.TYPE_CHAPTER)
                .parserMediaChapters(mediaId)
        }

        // API 格式
        val entities = if (UserHelper.isLogin) {
            BgmApiManager.bgmJsonApi.queryUserEp(
                subjectId = mediaId,
                offset = pager.first,
                limit = pager.second
            ).data.orEmpty().sortedBy { it.episode?.sort }
        } else {
            BgmApiManager.bgmJsonApi.querySubjectEp(
                subjectId = mediaId,
                offset = pager.first,
                limit = pager.second
            ).data.orEmpty()
                .map { ApiUserEpEntity(episode = it, type = EpCollectType.TYPE_NONE) }
                .sortedBy { it.episode?.sort }
        }

        return entities.map {
            val entity = it.episode ?: ApiEpisodeEntity()
            val insType = EpCollectType.toInterestType(it.type)

            MediaChapterEntity(
                id = entity.id.toString(),
                mediaId = entity.subjectId.toString(),
                titleCn = entity.nameCn.orEmpty(),
                titleNative = entity.name.orEmpty(),
                time = entity.airdate.orEmpty(),
                commentCount = entity.comment,
                airedStateText = "",
                isAired = false,
                isAiring = false,
                collectType = insType,
                collectStateText = "",
                epType = EpApiType.toEpType(entity.type),
                number = entity.ep.toString(),
                splitter = false
            )
        }
    }
}