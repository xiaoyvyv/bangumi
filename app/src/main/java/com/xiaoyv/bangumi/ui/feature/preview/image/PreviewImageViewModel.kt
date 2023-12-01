package com.xiaoyv.bangumi.ui.feature.preview.image

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel

/**
 * Class: [PreviewImageViewModel]
 *
 * @author why
 * @since 12/1/23
 */
class PreviewImageViewModel : BaseViewModel() {
    var totalImageUrls: MutableList<String> = mutableListOf()
    var currentImageUrl: String = ""

    val onImageListLiveData = MutableLiveData<Pair<Int, List<String>>>()

    override fun onViewCreated() {
        showImages()
    }

    private fun showImages() {
        if (!totalImageUrls.contains(currentImageUrl)) {
            totalImageUrls.add(0, currentImageUrl)
        }
        // 首图索引
        val showIndex = totalImageUrls.indexOf(currentImageUrl)
        onImageListLiveData.value = showIndex to totalImageUrls
    }
}