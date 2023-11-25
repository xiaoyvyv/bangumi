package com.xiaoyv.bangumi.ui.media

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel

/**
 * Class: [MediaViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class MediaViewModel : BaseViewModel() {

    internal val onOptionsItemLiveData = MutableLiveData<List<Any>?>()
}