package com.xiaoyv.bangumi.ui.discover.index

import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.ui.discover.index.binder.IndexGridBinder
import com.xiaoyv.bangumi.ui.discover.index.binder.IndexItemBinder
import com.xiaoyv.bangumi.ui.discover.index.binder.IndexTitleBinder

/**
 * Class: [IndexAdapter]
 *
 * @author why
 * @since 12/12/23
 */
class IndexAdapter : BaseMultiItemAdapter<IndexAdapter.IndexItem>() {

    init {
        this.addItemType(TYPE_GRID, IndexGridBinder())
            .addItemType(TYPE_TITLE, IndexTitleBinder())
            .addItemType(TYPE_ITEM, IndexItemBinder())
            .onItemViewType { position, list ->
                list[position].type
            }
    }

    data class IndexItem(
        var type: Int,
        var entity: Any,
    )

    companion object {
        const val TYPE_GRID = 1
        const val TYPE_TITLE = 2
        const val TYPE_ITEM = 3
    }
}