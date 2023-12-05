package com.xiaoyv.bangumi.ui.media.detail

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
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
    internal var mediaType: String = MediaType.TYPE_ANIME
    internal var mediaName: String = ""

    internal val onMediaDetailLiveData = MutableLiveData<MediaDetailEntity?>()

    internal val vpEnableLiveData = MutableLiveData(true)

    override fun onViewCreated() {

    }

}