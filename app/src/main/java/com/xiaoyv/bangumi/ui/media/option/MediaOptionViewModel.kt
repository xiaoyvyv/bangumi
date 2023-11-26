package com.xiaoyv.bangumi.ui.media.option

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.config.bean.MediaOptionConfig

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

    override fun onViewCreated() {
        val mediaOptions = MediaOptionConfig.buildMediaOptions()

        // Tile: 类型
        // Items: [xxx,xxx,xx]
        // Tile: 时间
        // Items: [xxx,xxx,xx]
        // Tile: 拼音
        // Items: [xxx,xxx,xx]
        val option = mediaOptions.find {
            it.mediaType == currentMediaType
        }?.option.orEmpty()

        val list = option.flatMap {
            val listOf = arrayListOf<Any>()
            listOf.add(it.title.orEmpty())
            listOf.addAll(it.items.orEmpty().onEach { item -> item.pathIndex = it.pathIndex })
            listOf
        }

        onOptionsItemLiveData.value = list
    }
}