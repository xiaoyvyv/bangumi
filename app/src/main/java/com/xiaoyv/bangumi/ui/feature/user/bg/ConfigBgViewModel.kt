package com.xiaoyv.bangumi.ui.feature.user.bg

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.UriUtils
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.response.GalleryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

/**
 * Class: [ConfigBgViewModel]
 *
 * @author why
 * @since 12/27/23
 */
class ConfigBgViewModel : BaseViewModel() {
    internal val onImageListLiveData = MutableLiveData<List<GalleryEntity>?>()

    /**
     * 用户配置的图片
     */
    internal var cacheImageLink = MutableLiveData<String>()

    override fun onViewCreated() {
        randomImage()
    }

    /**
     * 随机图片
     */
    fun randomImage() {
        launchUI {
            onImageListLiveData.value = withContext(Dispatchers.IO) {
                BgmApiManager.bgmJsonApi
                    .queryAnimePicture(page = Random.nextInt(0, 5000))
                    .posts.orEmpty()
                    .map {
                        GalleryEntity(
                            id = it.id,
                            width = it.width,
                            height = it.height,
                            size = it.size,
                            imageUrl = it.url,
                            largeImageUrl = it.largeUrl
                        )
                    }
            }
        }
    }

    fun handleImagePicture(uri: Uri) {
        launchUI {
            val file = withContext(Dispatchers.IO) {
                UriUtils.uri2File(uri)
            }

            cacheImageLink.value = file.absolutePath
        }
    }
}