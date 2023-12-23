package com.xiaoyv.bangumi.ui.media.detail.overview

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.MediaChapterEntity
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.api.parser.impl.parserMediaChapters
import com.xiaoyv.common.api.parser.impl.parserMediaDetail
import com.xiaoyv.common.api.response.douban.DouBanImageEntity
import com.xiaoyv.common.api.response.douban.DouBanPhotoEntity
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.EpType
import com.xiaoyv.common.config.annotation.InterestType
import com.xiaoyv.common.config.annotation.MediaDetailType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.config.bean.AdapterTypeItem
import com.xiaoyv.common.config.bean.SampleImageEntity
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.widget.grid.EpGridView
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
     * 刷新进度
     *
     * first: 章节数据
     * second: 本篇进度值
     */
    internal val onRefreshEpLiveData =
        MutableLiveData<Triple<List<MediaChapterEntity>, Int, Int>?>()

    /**
     * 对应的豆瓣预览图片ID
     */
    internal var targetId: String = ""

    /**
     * 媒体类型
     */
    private val requireMediaType: String
        get() = mediaDetailLiveData.value?.mediaType ?: MediaType.TYPE_UNKNOWN

    private val defaultImage by lazy {
        DouBanPhotoEntity.Photo(
            image = DouBanPhotoEntity.Image(large = DouBanImageEntity(url = "https://bgm.tv/pic/wallpaper/02.png"))
        )
    }

    fun queryMediaInfo() {
        queryMediaDetail()
    }

    private fun queryMediaDetail() {
        launchUI(
            stateView = loadingViewState,
            error = {
                it.printStackTrace()

                mediaDetailLiveData.value = null
                mediaBinderListLiveData.value = emptyList()
            },
            block = {
                val (mediaEntity, binderList) = withContext(Dispatchers.IO) {
                    val entity = BgmApiManager.bgmWebApi
                        .queryMediaDetail(mediaId, MediaDetailType.TYPE_OVERVIEW)
                        .parserMediaDetail()
                    entity.photos = listOf(defaultImage)
                    entity to buildBinderList(entity)
                }

                mediaDetailLiveData.value = mediaEntity
                mediaBinderListLiveData.value = binderList

                // 动画、音乐、三次元 继续查询章节数据
                if (mediaEntity.mediaType == MediaType.TYPE_ANIME || mediaEntity.mediaType == MediaType.TYPE_REAL || mediaEntity.mediaType == MediaType.TYPE_MUSIC) {
                    refreshEpList()
                }
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

    /**
     * 查询预览图片
     */
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
                    targetId = searchResult.cards.orEmpty()
                        .firstOrNull { it?.targetType != "explore" }?.targetId.orEmpty()
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
        mediaDetailLiveData.value?.progressMax = it.progressMax
        return mediaDetailLiveData.value
    }

    /**
     * 进度加一
     */
    fun progressIncrease(progress: Int, progressSecond: Int) {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()
            },
            block = {
                withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.updateMediaProgress(
                        mediaId = mediaId,
                        watch = progress.toString(),
                        watchedVols = progressSecond.toString()
                    )
                }

                // 动画或三次元才刷新章节
                if (requireMediaType == MediaType.TYPE_ANIME || requireMediaType == MediaType.TYPE_REAL) {
                    UserHelper.notifyActionChange(BgmPathType.TYPE_EP)
                }

                // 书籍章节单独更新
                if (requireMediaType == MediaType.TYPE_BOOK) {
                    onRefreshEpLiveData.value = Triple(emptyList(), progress, progressSecond)
                }
            }
        )
    }

    /**
     * 刷新章节信息
     */
    fun refreshEpList() {
        launchUI {
            onRefreshEpLiveData.value = withContext(Dispatchers.IO) {
                val chapterList = queryMediaEpList()
                val progress = chapterList.count {
                    // 计算本篇的看过或抛弃数目
                    it.epType == EpType.TYPE_MAIN && (it.collectType == InterestType.TYPE_DROPPED || it.collectType == InterestType.TYPE_COLLECT)
                }

                Triple(chapterList, progress, 0)
            }
        }
    }


    /**
     * 查询进度列表
     */
    private suspend fun queryMediaEpList(): List<MediaChapterEntity> {
        return withContext(Dispatchers.IO) {
            val list =
                BgmApiManager.bgmWebApi.queryMediaDetail(mediaId, MediaDetailType.TYPE_CHAPTER)
                    .parserMediaChapters(mediaId)

            val horSpanCount = EpGridView.SPAN_COUNT_HORIZONTAL

            // 是否另起一行
            if (ConfigHelper.isSplitEpList && EpGridView.isHorizontalGrid(list.size)) {
                val newList = arrayListOf<MediaChapterEntity>()
                list.forEach { item ->
                    if (item.splitter) {
                        val i = newList.size % horSpanCount
                        // 补位
                        if (i != 0) {
                            repeat(horSpanCount - i) {
                                newList.add(
                                    MediaChapterEntity(
                                        id = System.currentTimeMillis().toString(),
                                        splitter = true
                                    )
                                )
                            }
                        }
                        newList.add(item)
                    } else {
                        newList.add(item)
                    }
                }

                newList
            } else {
                list
            }
        }
    }
}