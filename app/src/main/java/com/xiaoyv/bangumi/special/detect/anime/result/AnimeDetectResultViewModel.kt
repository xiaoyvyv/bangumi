package com.xiaoyv.bangumi.special.detect.anime.result

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.common.api.response.anime.AnimeSourceEntity

/**
 * AnimeDetectResultViewModel
 *
 * @author why
 * @since 11/18/23
 */
class AnimeDetectResultViewModel : BaseViewModel() {

    var animeSourceEntity: AnimeSourceEntity? = null

    internal val onAnimeSourceListLiveData = MutableLiveData<List<AnimeSourceEntity.SourceResult>>()

    override fun onViewCreated() {
        onAnimeSourceListLiveData.value = animeSourceEntity?.result.orEmpty()
    }
}