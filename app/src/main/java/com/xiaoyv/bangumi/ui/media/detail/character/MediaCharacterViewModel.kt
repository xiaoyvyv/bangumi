package com.xiaoyv.bangumi.ui.media.detail.character

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.MediaCharacterEntity
import com.xiaoyv.common.api.parser.impl.parserMediaCharacters
import com.xiaoyv.common.config.annotation.MediaDetailType

/**
 * Class: [MediaCharacterViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class MediaCharacterViewModel : BaseListViewModel<MediaCharacterEntity>() {
    /**
     * 媒体ID
     */
    internal var mediaId: String = ""

    override suspend fun onRequestListImpl(): List<MediaCharacterEntity> {
        require(mediaId.isNotBlank()) { "媒体ID不存在" }
        return BgmApiManager.bgmWebApi.queryMediaDetail(
            mediaId = mediaId,
            type = MediaDetailType.TYPE_CHARACTER,
            page = current
        ).parserMediaCharacters()
    }
}