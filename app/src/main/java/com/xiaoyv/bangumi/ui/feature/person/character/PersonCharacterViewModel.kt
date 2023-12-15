package com.xiaoyv.bangumi.ui.feature.person.character

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.CharacterEntity
import com.xiaoyv.common.api.parser.impl.parserPersonVoices

/**
 * Class: [PersonCharacterViewModel]
 *
 * @author why
 * @since 12/4/23
 */
class PersonCharacterViewModel : BaseListViewModel<CharacterEntity>() {
    /**
     * 人物ID和是否为虚拟人物
     */
    internal var personId: String = ""
    internal var isVirtual: Boolean = false

    override suspend fun onRequestListImpl(): List<CharacterEntity> {
        return BgmApiManager.bgmWebApi.queryPersonWorkVoices(personId)
            .parserPersonVoices(personId, isVirtual)
    }
}