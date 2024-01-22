package com.xiaoyv.bangumi.ui.media.detail.score

import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.common.config.annotation.MediaType

/**
 * Class: [MediaScoreViewModel]
 *
 * @author why
 * @since 1/22/24
 */
class MediaScoreViewModel : BaseViewModel() {
    internal var forFriend: Boolean = false
    internal var mediaId: String = ""

    @MediaType
    internal var mediaType: String = MediaType.TYPE_UNKNOWN
}