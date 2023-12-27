package com.xiaoyv.bangumi.special.detect

import android.net.Uri
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.common.widget.setting.SearchOptionView

/**
 * ImageDetectViewModel
 *
 * @author why
 * @since 11/18/23
 */
abstract class ImageDetectViewModel : BaseViewModel() {
    open val searchOptions: List<SearchOptionView.Option>
        get() = emptyList()

    abstract fun onImageSelected(imageUri: Uri, selectedOptions: List<SearchOptionView.Option>)
}