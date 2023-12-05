package com.xiaoyv.bangumi.ui.feature.person.overview

import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.common.api.parser.entity.UserDetailEntity

/**
 * Class: [PersonOverviewAdapter]
 *
 * @author why
 * @since 12/5/23
 */
class PersonOverviewAdapter : BaseMultiItemAdapter<PersonOverviewAdapter.Item>() {
    init {

    }

    companion object {

    }

    data class Item(
        var mediaDetailEntity: UserDetailEntity,
        var type: Int,
        var title: String
    )
}