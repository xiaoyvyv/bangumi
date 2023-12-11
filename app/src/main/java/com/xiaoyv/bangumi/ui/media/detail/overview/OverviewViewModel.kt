package com.xiaoyv.bangumi.ui.media.detail.overview

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.MediaCollectForm
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.api.parser.impl.parserMediaDetail
import com.xiaoyv.common.api.response.douban.DouBanPhotoEntity
import com.xiaoyv.common.config.annotation.MediaDetailType
import com.xiaoyv.common.config.annotation.SampleImageGridClickType
import com.xiaoyv.common.config.bean.SampleAvatar
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
    internal val mediaBinderListLiveData = MutableLiveData<List<OverviewAdapter.Item>>()
    internal val onMediaPreviewLiveData = MutableLiveData<List<DouBanPhotoEntity.Photo>?>()

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
                    entity to buildBinderList(entity)
                }

                mediaDetailLiveData.value = mediaEntity
                mediaBinderListLiveData.value = binderList
            }
        )
    }

    private fun buildBinderList(entity: MediaDetailEntity): List<OverviewAdapter.Item> {
        val items = mutableListOf<OverviewAdapter.Item>()

        items.add(OverviewAdapter.Item(entity, OverviewAdapter.TYPE_SAVE, "收藏"))
        items.add(OverviewAdapter.Item(entity, OverviewAdapter.TYPE_EP, "章节"))
        items.add(OverviewAdapter.Item(entity, OverviewAdapter.TYPE_TAG, "标签"))
        items.add(OverviewAdapter.Item(entity, OverviewAdapter.TYPE_SUMMARY, "简介"))
        items.add(OverviewAdapter.Item(entity.photos, OverviewAdapter.TYPE_PREVIEW, "预览"))
        items.add(OverviewAdapter.Item(entity, OverviewAdapter.TYPE_DETAIL, "详情"))
        items.add(OverviewAdapter.Item(entity, OverviewAdapter.TYPE_RATING, "评分"))

        // 角色介绍
        if (entity.characters.isNotEmpty()) {
            items.add(
                OverviewAdapter.Item(
                    entity.characters,
                    OverviewAdapter.TYPE_CHARACTER,
                    "角色介绍"
                )
            )
        }

        // 相关的媒体条目
        if (entity.relativeMedia.isNotEmpty()) {
            items.add(
                OverviewAdapter.Item(
                    entity.relativeMedia,
                    OverviewAdapter.TYPE_RELATIVE,
                    "相关的条目"
                )
            )
        }

        // 谁在看这部动画
        if (entity.whoSee.isNotEmpty()) items.add(
            OverviewAdapter.Item(
                entity.whoSee.map {
                    SampleAvatar(
                        id = it.id,
                        image = it.avatar,
                        title = it.name,
                        desc = it.time,
                        type = SampleImageGridClickType.TYPE_USER
                    )
                },
                OverviewAdapter.TYPE_COLLECTOR, "谁在看这部动画"
            )
        )

        // 推荐本条目的目录
        if (entity.recommendIndex.isNotEmpty()) items.add(
            OverviewAdapter.Item(
                entity.recommendIndex.map {
                    SampleAvatar(
                        id = it.id,
                        image = it.userAvatar,
                        title = it.title,
                        desc = it.userName,
                        type = SampleImageGridClickType.TYPE_INDEX
                    )
                }, OverviewAdapter.TYPE_INDEX, "推荐本条目的目录"
            )
        )

        // 吐槽
        if (entity.comments.isNotEmpty()) {
            items.add(OverviewAdapter.Item(entity, OverviewAdapter.TYPE_COMMENT, "吐槽"))
        }

        return items
    }

    fun queryPhotos(mediaName: String?) {
        launchUI(
            error = {
                onMediaPreviewLiveData.value = listOf(DouBanPhotoEntity.Photo(loading = false))
            },
            block = {
                requireNotNull(mediaName)
                onMediaPreviewLiveData.value = withContext(Dispatchers.IO) {
                    val searchResult =
                        BgmApiManager.bgmJsonApi.queryDouBanSearchHint(mediaName, 10)
                    val item = searchResult.items.orEmpty().firstOrNull()
                    val targetId = item?.targetId.orEmpty()
                    BgmApiManager.bgmJsonApi.queryDouBanPhotoList(targetId, 6)
                        .photos.orEmpty()
                        .let {
                            it.ifEmpty { listOf(DouBanPhotoEntity.Photo(loading = false)) }
                        }
                }
            }
        )
    }

    /**
     * 刷新收藏模型
     */
    fun refreshCollectState(it: MediaCollectForm): MediaDetailEntity? {
        mediaDetailLiveData.value?.collectState = it
        return mediaDetailLiveData.value
    }
}