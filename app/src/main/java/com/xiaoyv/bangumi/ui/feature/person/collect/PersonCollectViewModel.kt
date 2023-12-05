package com.xiaoyv.bangumi.ui.feature.person.collect

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.api.parser.impl.parserPersonCollector

/**
 * Class: [PersonCollectViewModel]
 *
 * @author why
 * @since 12/4/23
 */
class PersonCollectViewModel : BaseListViewModel<MediaDetailEntity.MediaWho>() {
    /**
     * 人物ID和是否为虚拟人物
     */
    internal var personId: String = ""
    internal var isVirtual: Boolean = false

    override suspend fun onRequestListImpl(): List<MediaDetailEntity.MediaWho> {
        require(personId.isNotBlank()) { "人物不存在" }

        return if (isVirtual) {
            BgmApiManager.bgmWebApi.queryCharacterCollectUser(
                personId = personId,
                page = current
            ).parserPersonCollector()
        } else {
            BgmApiManager.bgmWebApi.queryPersonCollectUser(
                personId = personId,
                page = current
            ).parserPersonCollector()
        }
    }
}