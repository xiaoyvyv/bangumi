package com.xiaoyv.bangumi.ui.feature.magi.rank

import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.ui.feature.magi.rank.binder.MagiRankHeaderBinder
import com.xiaoyv.bangumi.ui.feature.magi.rank.binder.MagiRankItemBinder
import com.xiaoyv.common.config.bean.AdapterTypeItem

/**
 * Class: [MagiRankAdapter]
 *
 * @author why
 * @since 11/24/23
 */
class MagiRankAdapter : BaseMultiItemAdapter<AdapterTypeItem>() {

    init {
        this.addItemType(TYPE_HEADER, MagiRankHeaderBinder())
            .addItemType(TYPE_ITEM, MagiRankItemBinder())
            .onItemViewType { position, list ->
                list[position].type
            }
    }

    companion object {
        const val TYPE_HEADER = 1
        const val TYPE_ITEM = 2
    }
}
