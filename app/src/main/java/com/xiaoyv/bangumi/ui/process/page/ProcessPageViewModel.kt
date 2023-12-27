package com.xiaoyv.bangumi.ui.process.page

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.api.parser.impl.parserHomePageProcess
import com.xiaoyv.common.config.annotation.BgmPathType
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
        return CacheHelper.cacheProcess.let {
            if (mediaType == MediaType.TYPE_UNKNOWN) it else it.filter { entity -> entity.mediaType == mediaType }
        }
    }

    override suspend fun onRequestListImpl(): List<MediaDetailEntity> {
        require(UserHelper.isLogin) { "你还没有登录呢" }

        return BgmApiManager.bgmWebApi.queryHomePage().parserHomePageProcess()
            .let {
                if (mediaType == MediaType.TYPE_UNKNOWN) it else it.filter { entity -> entity.mediaType == mediaType }
            }
            .apply {
                CacheHelper.cacheProcess = this
            }
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