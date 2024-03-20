package com.xiaoyv.bangumi.special.mikan.resource

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.impl.parserMikanGroupInfo
import com.xiaoyv.common.api.response.anime.AnimeMagnetEntity

/**
 * Class: [MikanResourceViewModel]
 *
 * @author why
 * @since 3/20/24
 */
class MikanResourceViewModel : BaseListViewModel<AnimeMagnetEntity.Resource>() {
    internal var bangumiId: String = ""
    internal var subtitleGroupId: String = ""
    internal var subtitleGroupName: String = ""

    override suspend fun onRequestListImpl(): List<AnimeMagnetEntity.Resource> {
        return BgmApiManager.bgmWebApi.queryMikanGroupDetail(bangumiId, subtitleGroupId)
            .parserMikanGroupInfo(subtitleGroupName)
    }
}