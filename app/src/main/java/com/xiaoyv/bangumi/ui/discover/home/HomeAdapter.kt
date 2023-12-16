package com.xiaoyv.bangumi.ui.discover.home

import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.chad.library.adapter.base.BaseMultiItemAdapter.OnItemViewTypeListener
import com.xiaoyv.bangumi.ui.discover.home.binder.HomeBannerBinder
import com.xiaoyv.bangumi.ui.discover.home.binder.HomeCalendarBinder
import com.xiaoyv.bangumi.ui.discover.home.binder.HomeCardBinder
import com.xiaoyv.common.api.parser.entity.BgmMediaEntity
import com.xiaoyv.common.api.parser.entity.HomeIndexBannerEntity
import com.xiaoyv.common.api.parser.entity.HomeIndexCalendarEntity
import com.xiaoyv.common.api.parser.entity.HomeIndexCardEntity
import com.xiaoyv.common.config.bean.HomeIndexFeature
import com.xiaoyv.common.helper.callback.RecyclerItemTouchedListener

/**
 * Class: [HomeAdapter]
 *
 * @author why
 * @since 11/25/23
 */
class HomeAdapter(
    touchedListener: RecyclerItemTouchedListener,
    onClickMedia: (BgmMediaEntity) -> Unit,
    onClickFeature: (HomeIndexFeature) -> Unit,
) : BaseMultiItemAdapter<Any>() {
    private val imageCardViewPool by lazy { RecycledViewPool() }

    init {
        this.addItemType(TYPE_TOP_BANNER, HomeBannerBinder(onClickFeature))
            .addItemType(TYPE_CALENDAR_PREVIEW, HomeCalendarBinder(touchedListener, onClickMedia))
            .addItemType(
                TYPE_MEDIA_CARD,
                HomeCardBinder(imageCardViewPool, touchedListener, onClickMedia)
            )
            .onItemViewType(OnItemViewTypeListener { position, list ->
                return@OnItemViewTypeListener when (list[position]) {
                    is HomeIndexCardEntity -> TYPE_MEDIA_CARD
                    is HomeIndexCalendarEntity -> TYPE_CALENDAR_PREVIEW
                    is HomeIndexBannerEntity -> TYPE_TOP_BANNER
                    else -> 1
                }
            })
    }


    companion object {
        const val TYPE_TOP_BANNER = 0
        const val TYPE_CALENDAR_PREVIEW = 1
        const val TYPE_MEDIA_CARD = 2
    }
}