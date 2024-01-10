package com.xiaoyv.bangumi.ui.media.detail

import androidx.lifecycle.MutableLiveData
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.api.response.anime.AnimeMalSearchEntity
import com.xiaoyv.common.config.annotation.InterestType
import com.xiaoyv.common.config.annotation.MediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [MediaDetailViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class MediaDetailViewModel : BaseViewModel() {
    internal val onMalItemLiveData = MutableLiveData<AnimeMalSearchEntity.Item?>()

    /**
     * 媒体ID
     */
    internal var mediaId: String = ""
    internal var mediaName: String = ""

    /**
     * 媒体名称
     */
    internal val requireMediaName: String
        get() {
            val entity = onMediaDetailLiveData.value ?: return mediaName
            return entity.titleCn.ifBlank { entity.titleNative }.ifBlank { mediaName }
        }

    /**
     * 未锁定
     */
    internal val requireNotLocked: Boolean
        get() = onMediaDetailLiveData.value != null && onMediaDetailLiveData.value?.locked == false

    /**
     * 媒体类型
     */
    @MediaType
    internal val requireMediaType: String
        get() = onMediaDetailLiveData.value?.mediaType.orEmpty()

    /**
     * 当前用户对该媒体收藏类型
     */
    @InterestType
    internal val requireMediaCollectType: String
        get() = onMediaDetailLiveData.value?.collectState?.interest ?: InterestType.TYPE_UNKNOWN

    internal val onMediaDetailLiveData = MutableLiveData<MediaDetailEntity?>()

    internal val vpEnableLiveData = MutableLiveData(true)

    internal val vpCurrentItemType = UnPeekLiveData<String>()

    override fun onViewCreated() {

    }

    /**
     * 查询 MAL 网站
     */
    fun queryMalInfo() {
        launchUI(
            error = {
                it.printStackTrace()
            },
            block = {
                require(requireMediaType == MediaType.TYPE_ANIME) { "仅动画支持查询MAL评分" }

                // Mal 对应的数据
                onMalItemLiveData.value = withContext(Dispatchers.IO) {
                    val mediaName = onMediaDetailLiveData.value?.titleNative.orEmpty()
                    val searchItem =
                        BgmApiManager.bgmJsonApi.queryMalItems(mediaName).categories.orEmpty()
                            .find { it.type == "anime" }?.items.orEmpty()
                            .firstOrNull()

                    requireNotNull(searchItem) { "未查询到 MAL 对应的条目：$mediaName" }
                }
            }
        )
    }
}