package com.xiaoyv.bangumi.ui.discover.group

import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.ui.discover.group.binder.GroupItemGridBinder
import com.xiaoyv.bangumi.ui.discover.group.binder.GroupItemTopicBinder
import com.xiaoyv.common.api.parser.entity.TopicSampleEntity
import com.xiaoyv.common.config.bean.AdapterTypeItem
import com.xiaoyv.common.config.bean.SampleImageEntity

/**
 * Class: [GroupAdapter]
 *
 * @author why
 * @since 12/8/23
 */
class GroupAdapter(
    onClickGroupListener: (SampleImageEntity) -> Unit,
    onClickTopicListener: (TopicSampleEntity) -> Unit,
) : BaseMultiItemAdapter<AdapterTypeItem>() {
    private val viewPool by lazy { RecycledViewPool() }

    init {
        this.addItemType(TYPE_GROUP_HOT, GroupItemGridBinder(viewPool, onClickGroupListener))
            .addItemType(TYPE_GROUP_NEW, GroupItemGridBinder(viewPool, onClickGroupListener))
            .addItemType(TYPE_TOPIC, GroupItemTopicBinder(onClickTopicListener))
            .onItemViewType { position, list ->
                list[position].type
            }
    }

    companion object {
        const val TYPE_GROUP_HOT = 1
        const val TYPE_GROUP_NEW = 2
        const val TYPE_TOPIC = 3
    }
}