package com.xiaoyv.bangumi.ui.discover.mono

import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.ui.discover.mono.binder.MonoGridBinder
import com.xiaoyv.bangumi.ui.discover.mono.binder.MonoHeaderBinder
import com.xiaoyv.common.config.bean.AdapterTypeItem
import com.xiaoyv.common.helper.ConfigHelper

/**
 * Class: [MonoAdapter]
 *
 * @author why
 * @since 12/8/23
 */
class MonoAdapter : BaseMultiItemAdapter<AdapterTypeItem>() {

    init {
        this.addItemType(TYPE_GRID, MonoGridBinder())
            .addItemType(TYPE_TITLE, MonoHeaderBinder())
            .onItemViewType { position, list ->
                list[position].type
            }

        ConfigHelper.configAdapterAnimation(this)
    }

    companion object {
        const val TYPE_TITLE = 1
        const val TYPE_GRID = 2
    }
}