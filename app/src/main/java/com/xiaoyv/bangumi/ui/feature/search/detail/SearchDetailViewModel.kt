@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.ui.feature.search.detail

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.bean.SearchItem

/**
 * Class: [SearchDetailViewModel]
 *
 * @author why
 * @since 12/10/23
 */
class SearchDetailViewModel : BaseViewModel() {

    internal val currentSearchItem = MutableLiveData<SearchItem?>()

    /**
     * 关键词
     */
    internal val onKeyword = MutableLiveData<String>()
    internal val onKeywordChange = MutableLiveData<String>()

    /**
     * 目标的搜索 TAB 索引
     */
    val targetSearchTab: Int
        get() {
            return when (currentSearchItem.value?.pathType) {
                BgmPathType.TYPE_SEARCH_SUBJECT -> 0
                BgmPathType.TYPE_SEARCH_MONO -> 1
                BgmPathType.TYPE_SEARCH_TAG -> 2
                BgmPathType.TYPE_TOPIC -> 3
                BgmPathType.TYPE_INDEX -> 4
                else -> 0
            }
        }
}