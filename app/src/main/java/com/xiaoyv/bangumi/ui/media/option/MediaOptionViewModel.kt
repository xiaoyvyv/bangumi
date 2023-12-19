package com.xiaoyv.bangumi.ui.media.option

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.config.bean.MediaOptionConfig
import com.xiaoyv.common.kts.currentYear

/**
 * Class: [MediaOptionViewModel]
 *
 * @author why
 * @since 11/26/23
 */
class MediaOptionViewModel : BaseViewModel() {
    internal val onOptionsItemLiveData = MutableLiveData<List<Any>>()

    /**
     * 当前的显示的 TAB 类型
     */
    internal var currentMediaType: String = MediaType.TYPE_ANIME

    private var yearPage = 0
    private var yearPageCount = 18

    override fun onViewCreated() {
        refreshConfig()
    }

    fun yearUp() {
        yearPage += 1
        refreshConfig()
    }

    fun yearDown() {
        yearPage -= 1
        refreshConfig()
    }

    private fun refreshConfig() {
        // 设置年份起始时间
        val fromYear = (currentYear + 1) + yearPageCount * yearPage
        val option = MediaOptionConfig.buildMediaOptions(fromYear, yearPageCount)
            .find { it.mediaType == currentMediaType }?.option.orEmpty()

        val list = option.flatMap {
            val listOf = arrayListOf<Any>()
            listOf.add(it.title.orEmpty())
            listOf.addAll(it.items.orEmpty().onEach { item -> item.pathIndex = it.pathIndex })
            listOf
        }

        onOptionsItemLiveData.value = list
    }
}