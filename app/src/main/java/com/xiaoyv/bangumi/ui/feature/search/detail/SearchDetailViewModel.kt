@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.ui.feature.search.detail

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
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
}