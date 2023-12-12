package com.xiaoyv.bangumi.ui.timeline.page

import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.ui.timeline.page.binder.TimelineGridBinder
import com.xiaoyv.bangumi.ui.timeline.page.binder.TimelineMediaBinder
import com.xiaoyv.bangumi.ui.timeline.page.binder.TimelineTextBinder
import com.xiaoyv.common.api.parser.entity.TimelineEntity
import com.xiaoyv.common.config.annotation.TimelineAdapterType
import com.xiaoyv.common.widget.image.AnimeGridImageView

/**
 * Class: [TimelinePageAdapter]
 *
 * @author why
 * @since 11/25/23
 */
class TimelinePageAdapter(
    onGridItemClickListener: ((AnimeGridImageView.Image) -> Unit)? = null
) : BaseMultiItemAdapter<TimelineEntity>() {
    init {
        this.addItemType(TimelineAdapterType.TYPE_GRID, TimelineGridBinder(onGridItemClickListener))
            .addItemType(TimelineAdapterType.TYPE_TEXT, TimelineTextBinder())
            .addItemType(TimelineAdapterType.TYPE_MEDIA, TimelineMediaBinder())
            .onItemViewType { position, list -> list[position].adapterType }
    }
}