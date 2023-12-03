package com.xiaoyv.bangumi.ui.media.detail.overview

import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.chad.library.adapter.base.BaseMultiItemAdapter.OnItemViewTypeListener
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewBoardBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewCharacterBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewCommentBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewDetailBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewEpBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewIndexBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewMakerBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewPreviewBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewRatingBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewRelativeBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewReviewBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewSaveBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewSummaryBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewTagBinder
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.helper.RecyclerItemTouchedListener

/**
 * Class: [OverviewAdapter]
 *
 * @author why
 * @since 11/30/23
 */
class OverviewAdapter(touchedListener: RecyclerItemTouchedListener) :
    BaseMultiItemAdapter<OverviewAdapter.OverviewItem>() {

    init {
        this.addItemType(TYPE_SAVE, OverviewSaveBinder{})
            .addItemType(TYPE_EP, OverviewEpBinder(touchedListener) {})
            .addItemType(TYPE_TAG, OverviewTagBinder {})
            .addItemType(TYPE_SUMMARY, OverviewSummaryBinder())
            .addItemType(TYPE_PREVIEW, OverviewPreviewBinder())
            .addItemType(TYPE_DETAIL, OverviewDetailBinder())
            .addItemType(TYPE_RATING, OverviewRatingBinder())
            .addItemType(TYPE_CHARACTER, OverviewCharacterBinder(touchedListener) {})
            .addItemType(TYPE_MAKER, OverviewMakerBinder())
            .addItemType(TYPE_RELATIVE, OverviewRelativeBinder(touchedListener) {})
            .addItemType(TYPE_INDEX, OverviewIndexBinder(touchedListener) {})
            .addItemType(TYPE_REVIEW, OverviewReviewBinder(touchedListener) {})
            .addItemType(TYPE_BOARD, OverviewBoardBinder(touchedListener) {})
            .addItemType(TYPE_COMMENT, OverviewCommentBinder(touchedListener) {})
            .onItemViewType(OnItemViewTypeListener { position, list ->
                return@OnItemViewTypeListener list[position].type
            })
    }

    companion object {
        const val TYPE_SAVE = 1
        const val TYPE_EP = 2
        const val TYPE_TAG = 3
        const val TYPE_SUMMARY = 4
        const val TYPE_PREVIEW = 5
        const val TYPE_DETAIL = 6
        const val TYPE_RATING = 7
        const val TYPE_CHARACTER = 8
        const val TYPE_MAKER = 9
        const val TYPE_RELATIVE = 10
        const val TYPE_INDEX = 11
        const val TYPE_REVIEW = 12
        const val TYPE_BOARD = 13
        const val TYPE_COMMENT = 14
    }

    data class OverviewItem(
        var mediaDetailEntity: MediaDetailEntity,
        var type: Int,
        var title: String
    )
}