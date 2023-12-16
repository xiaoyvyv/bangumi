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

    /**
     * 我的回复和发布也是复用改类的逻辑
     */
    private val isMyTopic: Boolean
        get() = groupId == "my_reply" || groupId == "my_topic"

    override suspend fun onRequestListImpl(): List<TopicSampleEntity> {
        require(groupId.isNotBlank()) { "小组不存在" }

        // 如果是查询我的回复或发布
        if (isMyTopic) {
            val (groupName, topics) = BgmApiManager.bgmWebApi
                .queryUserTopicList(type = groupId, page = current)
                .parserGroupTopics(groupId)
            this.groupName = groupName
            return topics
        }

        // 小组名称和话题列表
        val (groupName, topics) = BgmApiManager.bgmWebApi
            .queryGroupTopicList(groupId = groupId, page = current)
            .parserGroupTopics(groupId)
        this.groupName = groupName
        return topics
    }
}