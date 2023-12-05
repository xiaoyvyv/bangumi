package com.xiaoyv.bangumi.ui.feature.person.cooperate

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.PersonEntity
import com.xiaoyv.common.api.parser.impl.parserPersonCooperate

/**
 * Class: [PersonCooperateViewModel]
 *
 * @author why
 * @since 12/4/23
 */
class PersonCooperateViewModel : BaseListViewModel<PersonEntity.RecentCooperate>() {
    /**
     * 人物ID和是否为虚拟人物
     */
    internal var personId: String = ""
    internal var isVirtual: Boolean = false

    override suspend fun onRequestListImpl(): List<PersonEntity.RecentCooperate> {
        return BgmApiManager.bgmWebApi.queryPersonCooperate(personId, current)
            .parserPersonCooperate()
    }
}