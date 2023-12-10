package com.xiaoyv.bangumi.ui

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel

/**
 * Class: [MainViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class MainViewModel : BaseViewModel() {
    internal val onDiscoverPageIndex = MutableLiveData<Int>()

    fun resetDiscoverIndex() {
        onDiscoverPageIndex.value = 0
    }
}