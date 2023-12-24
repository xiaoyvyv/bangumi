package com.xiaoyv.common.helper

import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.response.api.ApiUserEpEntity
import com.xiaoyv.common.config.annotation.EpCollectType

/**
 * Class: [FullQueryHelper]
 *
 * @author why
 * @since 12/24/23
 */
object FullQueryHelper {
    private const val LIMIT = 1000

    /**
     * 批量查询全部
     */
    suspend fun requestAllEpisode(mediaId: String, mediaType: String): List<ApiUserEpEntity> {
        val entities = arrayListOf<ApiUserEpEntity>()

        suspend fun request(page: Int): List<ApiUserEpEntity> {
            val offset = (page - 1) * LIMIT

            return if (UserHelper.isLogin) {
                BgmApiManager.bgmJsonApi.queryUserEp(
                    subjectId = mediaId,
                    offset = offset,
                    limit = LIMIT
                ).data.orEmpty().onEach { it.episode?.fillState(it.type, mediaType) }
            } else {
                BgmApiManager.bgmJsonApi.querySubjectEp(
                    subjectId = mediaId,
                    offset = offset,
                    limit = LIMIT
                ).data.orEmpty().map {
                    ApiUserEpEntity(
                        episode = it.fillState(EpCollectType.TYPE_NONE, mediaType),
                        type = EpCollectType.TYPE_NONE
                    )
                }
            }
        }

        var page = 1
        while (true) {
            val list = request(page)
            entities.addAll(list)
            // 没有更多了
            if (list.size < LIMIT) break
            page++
        }

        return entities
    }
}