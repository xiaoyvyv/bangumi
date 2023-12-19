package com.xiaoyv.bangumi.ui.media.detail.overview

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.api.parser.impl.parserMediaDetail
import com.xiaoyv.common.api.response.douban.DouBanPhotoEntity
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.MediaDetailType
import com.xiaoyv.common.config.bean.AdapterTypeItem
import com.xiaoyv.common.config.bean.EpSaveProgress
import com.xiaoyv.common.config.bean.SampleImageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [OverviewViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class OverviewViewModel : BaseViewModel() {
    internal var mediaId: String = ""

    internal val mediaDetailLiveData = MutableLiveData<MediaDetailEntity?>()
    internal val mediaBinderListLiveData = MutableLiveData<List<AdapterTypeItem>>()
    internal val onMediaPreviewLiveData = MutableLiveData<List<DouBanPhotoEntity.Photo>?>()

    /**
     * 媒体名称
     */
    internal val requireMediaName: String
        get() {
            val entity = mediaDetailLiveData.value ?: return "条目：$mediaId"
            return entity.titleCn.ifBlank { entity.titleNative }
        }

    /**
     * 用户的进度信息
     */
    internal val requireProgress: EpSaveProgress
        get() = EpSaveProgress(
            mediaId = mediaId,
            mediaName = requireMediaName,
            myProgress = mediaDetailLiveData.value?.myProgress ?: 0,
            totalProgress = mediaDetailLiveData.value?.totalProgress ?: 0
        )

    private val defaultImage by lazy {
        DouBanPhotoEntity.Photo(
            image = DouBanPhotoEntity.Image(
                large = DouBanPhotoEntity.ImageItem(
                    url = "https://bgm.tv/pic/wallpaper/02.png"
                )
            )
        )
    }

    fun queryMediaInfo() {
        launchUI(
            stateView = loadingViewState,
            error = {
                it.printStackTrace()

                mediaDetailLiveData.value = null
                mediaBinderListLiveData.value = emptyList()
            },
            block = {
                val (mediaEntity, binderList) = withContext(Dispatchers.IO) {
                    val entity = BgmApiManager.bgmWebApi.queryMediaDetail(
                        mediaId = mediaId,
                        type = MediaDetailType.TYPE_OVERVIEW
                    ).parserMediaDetail()
                    entity.photos = listOf(defaultImage)
                    entity to buildBinderList(entity)
                }

                mediaDetailLiveData.value = mediaEntity
                mediaBinderListLiveData.value = binderList
            }
        )
    }

    private fun buildBinderList(entity: MediaDetailEntity): List<AdapterTypeItem> {
        val items = mutableListOf<AdapterTypeItem>()

        items.add(AdapterTypeItem(entity, OverviewAdapter.TYPE_SAVE, "收藏"))
        items.add(AdapterTypeItem(entity, OverviewAdapter.TYPE_EP, "章节"))
        items.add(AdapterTypeItem(entity, OverviewAdapter.TYPE_TAG, "标签"))
        items.add(AdapterTypeItem(entity, OverviewAdapter.TYPE_SUMMARY, "简介"))
        items.add(AdapterTypeItem(entity.photos, OverviewAdapter.TYPE_PREVIEW, "预览"))
        items.add(AdapterTypeItem(entity, OverviewAdapter.TYPE_DETAIL, "详情"))
        items.add(AdapterTypeItem(entity, OverviewAdapter.TYPE_RATING, "评分"))

        // 角色介绍
        if (entity.characters.isNotEmpty()) {
            items.add(
                AdapterTypeItem(
                    entity.characters,
                    OverviewAdapter.TYPE_CHARACTER,
                    "角色介绍"
                )
            )
        }

        // 相关的媒体条目
        if (entity.relativeMedia.isNotEmpty()) {
            items.add(
                AdapterTypeItem(
                    entity.relativeMedia,
                    OverviewAdapter.TYPE_RELATIVE,
                    "相关的条目"
                )
            )
        }

        // 谁在看这部动画
        if (entity.whoSee.isNotEmpty()) items.add(
            AdapterTypeItem(
                entity.whoSee.map {
                    SampleImageEntity(
                        id = it.id,
                        image = it.avatar,
                        title = it.name,
                        desc = it.time,
                        type = BgmPathType.TYPE_USER
                    )
                },
                OverviewAdapter.TYPE_COLLECTOR, "谁在看这部动画"
            )
        )

        // 推荐本条目的目录
        if (entity.recommendIndex.isNotEmpty()) items.add(
            AdapterTypeItem(
                entity.recommendIndex.map {
                    SampleImageEntity(
                        id = it.id,
                        image = it.userAvatar,
                        title = it.title,
                        desc = it.userName,
                        type = BgmPathType.TYPE_INDEX
                    )
                }, OverviewAdapter.TYPE_INDEX, "推荐本条目的目录"
            )
        )

        // 吐槽
        if (entity.comments.isNotEmpty()) {
            items.add(AdapterTypeItem(entity, OverviewAdapter.TYPE_COMMENT, "吐槽"))
        }

        return items
    }

    fun queryPhotos(mediaName: String?) {
        launchUI(
            error = {
                it.printStackTrace()
                onMediaPreviewLiveData.value = listOf(defaultImage)
            },
            block = {
                requireNotNull(mediaName)
                onMediaPreviewLiveData.value = withContext(Dispatchers.IO) {
                    val searchResult = BgmApiManager.bgmJsonApi.queryDouBanSuggestion(mediaName)
                    val targetId = searchResult.cards?.firstOrNull()?.targetId.orEmpty()
                    BgmApiManager.bgmJsonApi.queryDouBanPhotoList(targetId)
                        .photos.orEmpty().let { it.ifEmpty { listOf(defaultImage) } }
                }
            }
        )
    }

    /**
     * 刷新收藏模型
     */
    fun refreshCollectState(it: MediaDetailEntity): MediaDetailEntity? {
        mediaDetailLiveData.value?.collectState = it.collectState
        mediaDetailLiveData.value?.totalProgress = it.totalProgress
        return mediaDetailLiveData.value
    }
}