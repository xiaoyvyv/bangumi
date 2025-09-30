package com.xiaoyv.bangumi.shared.data.repository

import androidx.paging.Pager
import com.xiaoyv.bangumi.shared.data.model.request.list.topic.ListTopicParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeTopic

interface TopicRepository {
    fun fetchTopicPager(param: ListTopicParam): Pager<Int, ComposeTopic>
}