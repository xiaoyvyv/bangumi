package com.xiaoyv.bangumi.ui.discover.group.topic

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.TopicSampleEntity
import com.xiaoyv.common.api.parser.impl.parserGroupTopics

/**
 * Class: [GroupTopicsViewModel]
 *
 * @author why
 * @since 12/8/23
 */
class GroupTopicsViewModel : BaseListViewModel<TopicSampleEntity>() {
    internal var groupId = ""
    internal var groupName = ""

    override suspend fun onRequestListImpl(): List<TopicSampleEntity> {
        require(groupId.isNotBlank()) { "小组不存在" }
        val (groupName, topics) = BgmApiManager.bgmWebApi.queryGroupTopicList(
            groupId = groupId,
            page = current
        ).parserGroupTopics(groupId)
        this.groupName = groupName
        return topics
    }
}