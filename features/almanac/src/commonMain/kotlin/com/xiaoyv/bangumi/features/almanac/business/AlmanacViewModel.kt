package com.xiaoyv.bangumi.features.almanac.business

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.utils.currentYear
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList

/**
 * [AlmanacViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class AlmanacViewModel(
    savedStateHandle: SavedStateHandle,
    val userManager: UserManager,
) : BaseViewModel<AlmanacState, AlmanacSideEffect, AlmanacEvent.Action>(savedStateHandle) {

    private val banners = persistentMapOf(
        2010 to "https://lain.bgm.tv/pic/photo/l/47/7e/837364_01L41.jpg",
        2011 to "https://lain.bgm.tv/pic/photo/l/47/7e/837364_4Ji9x.jpg",
        2012 to "https://lain.bgm.tv/pic/photo/l/47/7e/837364_1nP5H.jpg",
        2013 to "https://lain.bgm.tv/pic/photo/l/47/7e/837364_TVZCo.jpg",
        2014 to "https://lain.bgm.tv/pic/photo/l/47/7e/837364_T6mb0.jpg",
        2015 to "https://lain.bgm.tv/pic/photo/l/47/7e/837364_P0PCw.jpg",
        2016 to "https://lain.bgm.tv/pic/photo/l/47/7e/837364_5DmMZ.jpg",
        2017 to "https://lain.bgm.tv/pic/photo/l/47/7e/837364_y3g1G.jpg",
        2018 to "https://lain.bgm.tv/pic/photo/l/47/7e/837364_U1n8f.jpg",
        2019 to "https://lain.bgm.tv/pic/photo/l/47/7e/837364_41x4D.jpg",
        2020 to "https://lain.bgm.tv/pic/photo/l/47/7e/837364_nPgPi.jpg",
        2021 to "https://lain.bgm.tv/pic/photo/l/47/7e/837364_NizJ8.jpg",
        2022 to "https://lain.bgm.tv/pic/photo/l/47/7e/837364_ko256.jpg",
        2023 to "https://lain.bgm.tv/pic/photo/l/47/7e/837364_C6uhU.jpg",
        2024 to "https://lain.bgm.tv/pic/photo/l/47/7e/837364_37r4N.jpg",
    )

    override fun initSate(onCreate: Boolean) = AlmanacState(
        bgmHost = userManager.settings.network.bgmHost,
        almanacs = buildList { for (year in 2010..currentYear()) add(year to banners[year].orEmpty()) }
            .reversed()
            .toPersistentList()
    )

    override fun onEvent(event: AlmanacEvent.Action) {
        when (event) {
            is AlmanacEvent.Action.OnRefresh -> refresh(loading = event.loading)
        }
    }
}