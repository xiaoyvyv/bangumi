package com.xiaoyv.bangumi.special.detect.character.result

import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.common.api.response.anime.DetectCharacterEntity
import com.xiaoyv.common.api.response.anime.ListParcelableWrapEntity

/**
 * Class: [CharacterDetectResultViewModel]
 *
 * @author why
 * @since 11/21/23
 */
class CharacterDetectResultViewModel : BaseViewModel() {

    internal var detectCharacterResult: ListParcelableWrapEntity<DetectCharacterEntity>? = null
}