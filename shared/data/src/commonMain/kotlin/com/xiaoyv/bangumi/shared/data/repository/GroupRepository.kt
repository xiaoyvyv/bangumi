package com.xiaoyv.bangumi.shared.data.repository

import androidx.paging.Pager
import com.xiaoyv.bangumi.shared.data.model.request.list.group.ListGroupParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeGroup

interface GroupRepository {
    fun fetchGroupPager(param: ListGroupParam): Pager<Int, ComposeGroup>

    suspend fun fetchGroupDetail(name: String): Result<ComposeGroup>

    suspend fun submitJoinOrExitGroup(name: String, isJoin: Boolean): Result<ComposeGroup>
}

