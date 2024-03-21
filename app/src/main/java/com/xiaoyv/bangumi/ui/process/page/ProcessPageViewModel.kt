package com.xiaoyv.bangumi.ui.process.page

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.api.parser.impl.parserHomePageProcess
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.EpCollectType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.helper.CacheHelper
import com.xiaoyv.common.helper.UserHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [ProcessPageViewModel]
 *
 * @author why
 * @since 12/24/23
 */
class ProcessPageViewModel : BaseListViewModel<MediaDetailEntity>() {
    @MediaType
    var mediaType: String = MediaType.TYPE_UNKNOWN

    override suspend fun onRequestCacheImpl(): List<MediaDetailEntity> {
        return CacheHelper.cacheProcess
            .let {
                if (mediaType == MediaType.TYPE_UNKNOWN) it else it.filter { entity -> entity.mediaType == mediaType }
            }
            .sortMediaProcess()
    }

    override suspend fun onRequestListImpl(): List<MediaDetailEntity> {
        require(UserHelper.isLogin) { "你还没有登录呢" }

        return BgmApiManager.bgmWebApi.queryHomePage()
            .parserHomePageProcess()
            .let {
                CacheHelper.cacheProcess = it

                // 过滤类型
                if (mediaType == MediaType.TYPE_UNKNOWN) it else {
                    it.filter { entity -> entity.mediaType == mediaType }
                }
            }
            .sortMediaProcess()
    }

    private fun List<MediaDetailEntity>.sortMediaProcess(): List<MediaDetailEntity> {
        forEach { item ->
            // 是否是今日放送
            var isTodayAiring = false
            // 是否全部已看
            val isWholeWatched = item.progress == item.progressMax
            var actioned = 0
            var aired = 0

            item.epList?.forEach {
                if (it.episode?.infoState?.isAiring == true) {
                    isTodayAiring = true
                }
                if (it.episode?.infoState?.isAired == true) {
                    aired++
                }
                if (it.type != EpCollectType.TYPE_NONE) {
                    actioned++
                }
            }

            // 是否还有已经更新的但是没看的条目
            val hasUnWatch = actioned != aired && aired != 0

            if (isTodayAiring) {
                item.customSort = 5f
            } else if (hasUnWatch) {
                item.customSort = 3f + (actioned / aired.toFloat())
            }

            // 全部看完下沉
            if (isWholeWatched && !isTodayAiring) {
                item.customSort = -1f
            }
        }
        return sortedByDescending { it.customSort }
    }

    /**
     * 进度加一
     */
    fun progressIncrease(entity: MediaDetailEntity, progress: Int, progressSecond: Int) {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()
            },
            block = {
                withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.updateMediaProgress(
                        mediaId = entity.id,
                        watch = progress.toString(),
                        watchedVols = progressSecond.toString()
                    )
                }

                UserHelper.notifyActionChange(BgmPathType.TYPE_EP)
            }
        )
    }
}

