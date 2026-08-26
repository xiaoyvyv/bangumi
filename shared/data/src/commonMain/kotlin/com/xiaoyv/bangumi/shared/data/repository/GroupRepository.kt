package com.xiaoyv.bangumi.shared.data.repository

import com.xiaoyv.bangumi.shared.data.model.request.list.group.ListGroupParam
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeId
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeGroup
import com.xiaoyv.bangumi.shared.data.repository.datasource.MemoryPagingController

interface GroupRepository {
    fun fetchGroupPager(param: ListGroupParam): MemoryPagingController<ComposeGroup, String>

    suspend fun fetchGroupDetail(name: String): Result<ComposeGroup>

    suspend fun submitJoinOrExitGroup(name: String, isJoin: Boolean): Result<ComposeGroup>

    suspend fun submitCreateGroupTopic(
        groupName: String,
        title: String,
        content: String,
        turnstile: String,
    ): Result<ComposeId>
}
