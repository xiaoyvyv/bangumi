package com.xiaoyv.bangumi.ui.feature.user.bg

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.UriUtils
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xiaoyv.bangumi.ui.profile.edit.EditProfileViewModel.Companion.addSettingForms
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.SettingBaseEntity
import com.xiaoyv.common.api.parser.impl.parserSettingUpdateResult
import com.xiaoyv.common.api.response.GalleryEntity
import com.xiaoyv.common.helper.CacheHelper
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.widget.kts.errorMsg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
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
     * 更新结果
     */
    internal val onSaveBgResultLiveData = UnPeekLiveData<String?>()

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
        launchUI(stateView = loadingViewState) {
            onImageListLiveData.value = withContext(Dispatchers.IO) {
                // 首次加载缓存
                if (onImageListLiveData.value == null) {
                    val imageList = CacheHelper.cacheBgImageList
                    if (imageList.isNotEmpty()) {
                        return@withContext imageList
                    }
                }

                BgmApiManager.bgmJsonApi
                    .queryAnimePicture(
                        deniedTags = ConfigHelper.searchImagePicDeniedTags,
                        page = Random.nextInt(0, 3000)
                    )
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
                    .apply {
                        CacheHelper.cacheBgImageList = this
                    }
            }
        }
    }

    /**
     * 随机图片
     */
    fun handleImagePicture(uri: Uri) {
        launchUI {
            val file = withContext(Dispatchers.IO) {
                UriUtils.uri2File(uri)
            }

            cacheImageLink.value = file.absolutePath
        }
    }

    /**
     * 保存图片到个人简介
     */
    fun saveSingBg(link: String) {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()
                onSaveBgResultLiveData.value = it.errorMsg
            },
            block = {
                onSaveBgResultLiveData.value = withContext(Dispatchers.IO) {
                    val nickName = UserHelper.currentUser.nickname
                    val summary = buildSummaryWithRoomPic(link)
                    val params = listOf(
                        SettingBaseEntity(field = "formhash", value = UserHelper.formHash),
                        SettingBaseEntity(field = "nickname", value = nickName),
                        SettingBaseEntity(field = "newbio", value = summary),
                        SettingBaseEntity(field = "submit", value = "保存修改")
                    )

                    val multipartBody = MultipartBody.Builder()
                        .setType("multipart/form-data".toMediaType())
                        .addSettingForms(params)
                        .build()

                    BgmApiManager.bgmWebApi
                        .updateSettings(multipartBody)
                        .parserSettingUpdateResult()
                        .apply {
                            UserHelper.refresh()
                        }
                }
            }
        )
    }

    /**
     * 像简介插入背景描述
     *
     * ```html
     * [size=0][bg]https://xxx.xxx[/bg][/size]
     * ```
     */
    private fun buildSummaryWithRoomPic(link: String): String {
        val regex = "\\[size=0]\\[bg]\\s*(.*?)\\s*\\[/bg]\\s*\\[/size]".toRegex()
        val summary = UserHelper.currentUser.summary
        val roomBgCode = "[size=0][bg]${link.trim()}[/bg][/size]"
        val targetSummary = if (summary.contains(regex)) {
            summary.replace(regex, roomBgCode)
        } else {
            summary + "\n\n" + roomBgCode
        }
        return targetSummary
    }
}