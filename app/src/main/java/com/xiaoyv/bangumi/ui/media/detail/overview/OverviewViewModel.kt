package com.xiaoyv.bangumi.ui.media.detail.overview

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.MediaCollectForm
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.api.parser.impl.parserMediaDetail
import com.xiaoyv.common.config.annotation.MediaDetailType
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
    internal val mediaBinderListLiveData = MutableLiveData<List<OverviewAdapter.OverviewItem>>()

    fun queryMediaInfo() {
        launchUI(
            stateView = loadingViewState,
            error = {
                mediaDetailLiveData.value = null
                it.printStackTrace()
            },
            block = {
                val (mediaEntity, binderList) = withContext(Dispatchers.IO) {
                    val mediaEntity = BgmApiManager.bgmWebApi.queryMediaDetail(
                        mediaId = mediaId,
                        type = MediaDetailType.TYPE_OVERVIEW
                    ).parserMediaDetail()

                    mediaEntity to buildBinderList(mediaEntity)
                }

                mediaDetailLiveData.value = mediaEntity
                mediaBinderListLiveData.value = binderList
            }
        )
    }

    private fun buildBinderList(entity: MediaDetailEntity): List<OverviewAdapter.OverviewItem> =
        listOf(
            OverviewAdapter.OverviewItem(entity, OverviewAdapter.TYPE_SAVE, "收藏"),
            OverviewAdapter.OverviewItem(entity, OverviewAdapter.TYPE_EP, "章节"),
            OverviewAdapter.OverviewItem(entity, OverviewAdapter.TYPE_TAG, "标签"),
            OverviewAdapter.OverviewItem(entity, OverviewAdapter.TYPE_SUMMARY, "简介"),
            OverviewAdapter.OverviewItem(entity, OverviewAdapter.TYPE_PREVIEW, "预览"),
            OverviewAdapter.OverviewItem(entity, OverviewAdapter.TYPE_DETAIL, "详情"),
            OverviewAdapter.OverviewItem(entity, OverviewAdapter.TYPE_RATING, "评分"),
            OverviewAdapter.OverviewItem(entity, OverviewAdapter.TYPE_CHARACTER, "人物"),
//            OverviewAdapter.OverviewItem(entity, OverviewAdapter.TYPE_MAKER, "制作人员"),
            OverviewAdapter.OverviewItem(entity, OverviewAdapter.TYPE_RELATIVE, "相关的条目"),
            OverviewAdapter.OverviewItem(entity, OverviewAdapter.TYPE_INDEX, "推荐的目录"),
            OverviewAdapter.OverviewItem(entity, OverviewAdapter.TYPE_REVIEW, "评论"),
            OverviewAdapter.OverviewItem(entity, OverviewAdapter.TYPE_BOARD, "讨论板"),
            OverviewAdapter.OverviewItem(entity, OverviewAdapter.TYPE_COMMENT, "吐槽")
        )

    /**
     * 刷新收藏模型
     */
    fun refreshCollectState(it: MediaCollectForm) {
        mediaDetailLiveData.value?.collectState = it
        mediaDetailLiveData.value = mediaDetailLiveData.value
    }
}