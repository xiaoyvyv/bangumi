package com.xiaoyv.bangumi.ui.media.detail

import androidx.lifecycle.MutableLiveData
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.config.annotation.InterestType
import com.xiaoyv.common.config.annotation.MediaType

/**
 * Class: [MediaDetailViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class MediaDetailViewModel : BaseViewModel() {

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
    internal val requireMediaCollectType: String
        @InterestType
        get() = onMediaDetailLiveData.value?.collectState?.interest ?: InterestType.TYPE_UNKNOWN

    internal val onMediaDetailLiveData = MutableLiveData<MediaDetailEntity?>()

    internal val vpEnableLiveData = MutableLiveData(true)

    internal val vpCurrentItemType = UnPeekLiveData<String>()

    override fun onViewCreated() {

    }

}