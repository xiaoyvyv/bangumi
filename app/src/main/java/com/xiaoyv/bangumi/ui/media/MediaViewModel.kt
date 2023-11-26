package com.xiaoyv.bangumi.ui.media

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.common.config.bean.MediaOptionConfig

/**
 * Class: [MediaViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class MediaViewModel : BaseViewModel() {
    internal val currentSelectedOptionItem =
        MutableLiveData<MutableMap<String, List<MediaOptionConfig.Config.Option.Item>>>(mutableMapOf())
}