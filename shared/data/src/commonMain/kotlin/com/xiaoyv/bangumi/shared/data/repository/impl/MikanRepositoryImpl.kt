package com.xiaoyv.bangumi.shared.data.repository.impl

import androidx.paging.Pager
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.shared.core.utils.runResult
import com.xiaoyv.bangumi.shared.data.api.client.BgmApiClient
import com.xiaoyv.bangumi.shared.data.model.request.SearchMagnetBody
import com.xiaoyv.bangumi.shared.data.model.response.mikan.ComposeMikanGroup
import com.xiaoyv.bangumi.shared.data.model.response.mikan.ComposeMikanResource
import com.xiaoyv.bangumi.shared.data.parser.MikanParser
import com.xiaoyv.bangumi.shared.data.repository.MikanRepository
import com.xiaoyv.bangumi.shared.data.repository.datasource.createNetworkPageLimitPagingPager
import com.xiaoyv.bangumi.shared.data.repository.datasource.createPagingConfig
import kotlinx.serialization.json.Json

/**
 * [MikanRepositoryImpl]
 *
 * @author why
 * @since 2025/1/23
 */
class MikanRepositoryImpl(
    private val client: BgmApiClient,
    private val mikanParser: MikanParser,
    private val json: Json,
) : MikanRepository {
    override fun fetchGardenResourcePager(param: SearchMagnetBody): Pager<Int, ComposeMikanResource> {
        return createNetworkPageLimitPagingPager(
            pagingConfig = createPagingConfig(20),
            keySelector = { it.title },
            onLoadData = {
                with(mikanParser) {
                    client.requestMikanApi {
                        fetchGardenResource(
                            page = it,
                            keyword = param.keyword,
                            sortId = param.typeId,
                            teamId = param.teamId,
                            order = param.order
                        ).fetchGardenResourceConverted()
                    }.getOrThrow()
                }
            }
        )
    }

    override suspend fun fetchMikanIdMapByEmbed(): Result<Map<String, String>> = runResult {
        val text = Res.readBytes("files/mikan/mikan.json").decodeToString()
        json.decodeFromString<Map<String, String>>(text)
    }

    override suspend fun fetchMikanIdMapByJsdelivr(): Result<Map<String, String>> = runResult {
        client.mikanApi.fetchMikanIdMapByJsdelivr()
    }

    override suspend fun fetchMikanIdMapByGithub(): Result<Map<String, String>> = runResult {
        client.mikanApi.fetchMikanIdMapByGithub()
    }

    override suspend fun fetchMikanGroup(mikanId: String): Result<List<ComposeMikanGroup>> =
        runResult {
            with(mikanParser) {
                client.mikanApi
                    .fetchMikanGroup(mikanId)
                    .fetchMikanGroupConverted()
            }
        }

    override suspend fun fetchMikanResources(
        mikanId: String,
        subtitleGroupId: String,
        subtitleGroupName: String,
    ): Result<List<ComposeMikanResource>> = runResult {
        with(mikanParser) {
            client.mikanApi
                .fetchMikanResources(mikanId, subtitleGroupId)
                .fetchMikanResourcesConverted(subtitleGroupName)
        }
    }
}