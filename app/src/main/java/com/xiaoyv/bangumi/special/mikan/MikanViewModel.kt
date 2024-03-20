package com.xiaoyv.bangumi.special.mikan

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.MikanEntity
import com.xiaoyv.common.api.parser.impl.parserMikanInfo
import com.xiaoyv.widget.kts.sendValue

/**
 * Class: [MikanViewModel]
 *
 * @author why
 * @since 3/20/24
 */
class MikanViewModel : BaseListViewModel<MikanEntity.Group>() {
    internal var mikanId: String = ""

    internal val onMikanInfoLiveData = MutableLiveData<MikanEntity?>()

    override suspend fun onRequestListImpl(): List<MikanEntity.Group> {
        val info = BgmApiManager.bgmWebApi.queryMikanDetail(mikanId)
            .parserMikanInfo(mikanId)

        onMikanInfoLiveData.sendValue(info)

        return info.groups.orEmpty().onEach {
            it.mikanId = mikanId
        }
    }
}