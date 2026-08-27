package com.xiaoyv.bangumi.shared.data.repository

import com.xiaoyv.bangumi.shared.core.types.RakuenType
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeDollarItem
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeGroupHomepage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeStatus
import com.xiaoyv.bangumi.shared.data.model.response.bgm.rakuen.ComposeRakuenTopic
import com.xiaoyv.bangumi.shared.data.repository.datasource.MemoryPagingController

interface UgcRepository {
    fun fetchRaKuenPager(@RakuenType type: String, filter: String? = null): MemoryPagingController<ComposeRakuenTopic, String>



    suspend fun fetchDollarsChat(): Result<List<ComposeDollarItem>>

    suspend fun fetchGroupHomepage(): Result<ComposeGroupHomepage>

    suspend fun summitDollarsChat(message: String): Result<ComposeStatus>
}
