package com.xiaoyv.bangumi.ui.media.detail.overview

import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.chad.library.adapter.base.BaseMultiItemAdapter.OnItemViewTypeListener
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewBookBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewCharacterBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewCommentBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewEpBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewGridBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewPreviewBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewRatingBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewRelativeBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewSaveBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewSummaryBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewTagBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewTourBinder
import com.xiaoyv.common.api.parser.entity.MediaCommentEntity
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.api.response.anime.AnimeTourEntity
import com.xiaoyv.common.api.response.api.ApiUserEpEntity
import com.xiaoyv.common.api.response.douban.DouBanPhotoEntity
import com.xiaoyv.common.config.bean.AdapterTypeItem
import com.xiaoyv.common.config.bean.SampleImageEntity
import com.xiaoyv.common.helper.callback.RecyclerItemTouchedListener

/**
 * Class: [OverviewAdapter]
 *
 * @author why
 * @since 11/30/23
 */
class OverviewAdapter(
    touchedListener: RecyclerItemTouchedListener,
    onClickSave: (AdapterTypeItem, Int) -> Unit,
    onClickEpItem: OnItemChildClickListener<ApiUserEpEntity>,
    onClickEpAdd: (MediaDetailEntity, Boolean) -> Unit,
    onClickCrtItem: (MediaDetailEntity.MediaCharacter) -> Unit,
    onClickTagItem: (MediaDetailEntity.MediaTag) -> Unit,
    onClickRelatedItem: (MediaDetailEntity.MediaRelative) -> Unit,
    onClickCollectorItem: (SampleImageEntity) -> Unit,
    onClickIndexItem: (SampleImageEntity) -> Unit,
    onClickPreview: (DouBanPhotoEntity.Photo) -> Unit,
    onClickTour: (AnimeTourEntity.LitePoint) -> Unit,
    onClickCommentItem: (MediaCommentEntity) -> Unit,
    onClickBookItem: (MediaDetailEntity.MediaRelative) -> Unit,
    onClickCommentUser: (MediaCommentEntity) -> Unit,
) : BaseMultiItemAdapter<AdapterTypeItem>() {

    init {
        val gridPool = RecyclerView.RecycledViewPool()

        this.addItemType(TYPE_COLLECT, OverviewSaveBinder(onClickSave))
            .addItemType(TYPE_EP, OverviewEpBinder(touchedListener, onClickEpItem, onClickEpAdd))
            .addItemType(TYPE_TAG, OverviewTagBinder(onClickTagItem))
            .addItemType(TYPE_SUMMARY, OverviewSummaryBinder(true))
            .addItemType(TYPE_PREVIEW, OverviewPreviewBinder(touchedListener, onClickPreview))
            .addItemType(TYPE_TOUR, OverviewTourBinder(touchedListener, onClickTour))
            .addItemType(TYPE_DETAIL, OverviewSummaryBinder(false))
            .addItemType(TYPE_RATING, OverviewRatingBinder())
            .addItemType(TYPE_CHARACTER, OverviewCharacterBinder(onClickCrtItem))
            .addItemType(TYPE_RELATIVE, OverviewRelativeBinder(touchedListener, onClickRelatedItem))
            .addItemType(
                TYPE_COLLECT_USER,
                OverviewGridBinder(gridPool, touchedListener, onClickCollectorItem)
            )
            .addItemType(
                TYPE_INDEX,
                OverviewGridBinder(gridPool, touchedListener, onClickIndexItem)
            )
            .addItemType(
                TYPE_COMMENT,
                OverviewCommentBinder(touchedListener, onClickCommentItem, onClickCommentUser)
            )
            .addItemType(TYPE_BOOK, OverviewBookBinder(touchedListener, onClickBookItem))
            .onItemViewType(OnItemViewTypeListener { position, list ->
                return@OnItemViewTypeListener list[position].type
            })
    }

    /**
     * 刷新预览板块
     */
    fun refreshPhotos(photos: List<DouBanPhotoEntity.Photo>) {
        val item = items.find { it.type == TYPE_PREVIEW } ?: return
        val targetIndex = itemIndexOfFirst(item)
        if (targetIndex != -1 && photos.isNotEmpty()) {
            item.entity = photos
            set(targetIndex, item)
        }
    }

    /**
     * 刷新进度板块
     */
    fun refreshEpList(chapters: List<ApiUserEpEntity>, progress: Int, progressSecond: Int) {
        val item = items.find { it.type == TYPE_EP } ?: return
        val targetIndex = itemIndexOfFirst(item)
        val entity = item.entity as MediaDetailEntity
        if (targetIndex != -1) {
            entity.epList = chapters
            entity.progress = progress
            entity.progressSecond = progressSecond
            item.entity = entity
            set(targetIndex, item)
        }
    }

    /**
     * 刷新收藏板块
     */
    fun refreshCollect(entity: MediaDetailEntity) {
        val item = items.find { it.type == TYPE_COLLECT } ?: return
        val targetIndex = itemIndexOfFirst(item)
        if (targetIndex != -1) {
            item.entity = entity
            // 刷新条目收藏的板块
            set(targetIndex, item)
        }

        // 刷新条目收藏的板块下一个、章节格子板块
        val epiItem = items.find { it.type == TYPE_EP } ?: return
        epiItem.entity = entity
        set(targetIndex + 1, epiItem)
    }

    /**
     * 刷新巡礼数据
     */
    fun refreshTour(tourEntity: AnimeTourEntity) {
        val previewItemIndex = items.indexOfFirst { it.type == TYPE_PREVIEW }
        if (previewItemIndex == -1) return
        val item = items.find { it.type == TYPE_TOUR }
        if (item == null) {
            val typeItem =
                AdapterTypeItem(entity = tourEntity, type = TYPE_TOUR, title = "巡礼")
            add(previewItemIndex + 1, typeItem)
        }
    }

    companion object {
        const val TYPE_COLLECT = 1
        const val TYPE_EP = 2
        const val TYPE_TAG = 3
        const val TYPE_SUMMARY = 4
        const val TYPE_PREVIEW = 5
        const val TYPE_DETAIL = 6
        const val TYPE_RATING = 7
        const val TYPE_CHARACTER = 8
        const val TYPE_RELATIVE = 9
        const val TYPE_COLLECT_USER = 10
        const val TYPE_INDEX = 11
        const val TYPE_COMMENT = 12
        const val TYPE_TOUR = 13
        const val TYPE_BOOK = 14
    }
}