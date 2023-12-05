package com.xiaoyv.bangumi.ui.feature.person.opus

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.PersonEntity
import com.xiaoyv.common.api.parser.impl.parserPersonOpus

/**
 * Class: [PersonOpusViewModel]
 *
 * @author why
 * @since 12/4/23
 */
class PersonOpusViewModel : BaseListViewModel<PersonEntity.RecentlyOpus>() {
    /**
     * 人物ID和是否为虚拟人物
     */
    internal var personId: String = ""
    internal var isVirtual: Boolean = false

    override suspend fun onRequestListImpl(): List<PersonEntity.RecentlyOpus> {
        return BgmApiManager.bgmWebApi.queryPersonWorks(personId, current).parserPersonOpus()
    }
}