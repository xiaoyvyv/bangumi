package com.xiaoyv.bangumi.ui.media.detail.overview.binder

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.ui.media.detail.overview.OverviewAdapter
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.config.bean.AdapterTypeItem
import com.xiaoyv.common.kts.forceCast
import com.xiaoyv.common.widget.state.AnimeSummaryView

/**
 * Class: [OverviewSummaryBinder]
 *
 * @author why
 * @since 11/30/23
 */
class OverviewSummaryBinder(private val isSummary: Boolean) :
    BaseMultiItemAdapter.OnMultiItemAdapterListener<AdapterTypeItem, AnimeSummaryView.Holder> {

    override fun onBind(
        holder: AnimeSummaryView.Holder,
        position: Int,
        item: AdapterTypeItem?
    ) {
        item ?: return
        holder.summaryView.section.title = item.title

        item.entity.forceCast<MediaDetailEntity>().apply {
            if (isSummary) {
                holder.summaryView.summary = subjectSummary
            } else {
                holder.summaryView.summaries = infoShort
            }
        }
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ) = AnimeSummaryView.createHolder(context, parent)
}