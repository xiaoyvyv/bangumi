package com.xiaoyv.bangumi.shared.data.repository

import com.xiaoyv.bangumi.shared.data.model.request.SearchMagnetBody
import com.xiaoyv.bangumi.shared.data.model.response.mikan.ComposeMikanGroup
import com.xiaoyv.bangumi.shared.data.model.response.mikan.ComposeMikanResource
import com.xiaoyv.bangumi.shared.data.repository.datasource.MemoryPagingController

/**
 * [MikanRepository]
 *
 * @author why
 * @since 2025/1/14
 */
interface MikanRepository {
    fun fetchGardenResourcePager(param: SearchMagnetBody): MemoryPagingController<ComposeMikanResource, String>

    suspend fun fetchMikanIdMapByEmbed(): Result<Map<String, String>>

    suspend fun fetchMikanIdMapByJsdelivr(): Result<Map<String, String>>

    suspend fun fetchMikanIdMapByGithub(): Result<Map<String, String>>

    suspend fun fetchMikanResources(
        mikanId: String,
        subtitleGroupId: String,
        subtitleGroupName: String,
    ): Result<List<ComposeMikanResource>>

    suspend fun fetchMikanGroup(mikanId: String): Result<List<ComposeMikanGroup>>

}
