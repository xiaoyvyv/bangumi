package com.xiaoyv.bangumi.ui.feature.person.overview.binder

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.ui.feature.person.overview.PersonOverviewAdapter
import com.xiaoyv.common.api.parser.entity.PersonEntity
import com.xiaoyv.common.kts.forceCast
import com.xiaoyv.common.widget.state.AnimeSummaryView

/**
 * Class: [PersonOverviewSummaryBinder]
 *
 * @author why
 * @since 11/30/23
 */
class PersonOverviewSummaryBinder(private val isSummary: Boolean) :
    BaseMultiItemAdapter.OnMultiItemAdapterListener<PersonOverviewAdapter.Item, AnimeSummaryView.Holder> {

    override fun onBind(
        holder: AnimeSummaryView.Holder,
        position: Int,
        item: PersonOverviewAdapter.Item?
    ) {
        item ?: return
        holder.summaryView.section.title = item.title

        item.entity.forceCast<PersonEntity>().apply {
            if (isSummary) {
                holder.summaryView.summary = summary
            } else {
                holder.summaryView.summaries = infos.map { it.title + "：" + it.value }
            }
        }
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ) = AnimeSummaryView.createHolder(context, parent)
}