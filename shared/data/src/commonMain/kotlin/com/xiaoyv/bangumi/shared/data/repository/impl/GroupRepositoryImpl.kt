package com.xiaoyv.bangumi.shared.data.repository.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.xiaoyv.bangumi.shared.core.types.list.ListGroupType
import com.xiaoyv.bangumi.shared.core.utils.requireNoError
import com.xiaoyv.bangumi.shared.data.api.client.BgmApiClient
import com.xiaoyv.bangumi.shared.data.manager.app.PreferenceStore
import com.xiaoyv.bangumi.shared.data.model.request.list.group.ListGroupParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeGroup
import com.xiaoyv.bangumi.shared.data.repository.GroupRepository
import com.xiaoyv.bangumi.shared.data.repository.datasource.createNetworkOffsetLimitPagingPager

class GroupRepositoryImpl(
    private val client: BgmApiClient,
    private val pagingConfig: PagingConfig,
    private val preferenceStore: PreferenceStore,
) : GroupRepository {

    override fun fetchGroupPager(param: ListGroupParam): Pager<Int, ComposeGroup> {
        return createNetworkOffsetLimitPagingPager(
            pagingConfig = pagingConfig,
            keySelector = { it.name },
            onLoadData = {
                when (param.type) {
                    ListGroupType.BROWSER -> client.nextGroupApi.getGroups(
                        mode = param.browser.mode,
                        sort = param.browser.sort,
                        offset = it,
                        limit = pagingConfig.pageSize,
                    ).result

                    ListGroupType.USER -> client.nextUserApi.getUserGroups(
                        username = param.username,
                        offset = it,
                        limit = pagingConfig.pageSize
                    ).result

                    else -> error("暂不支持该类型")
                }
            }
        )
    }

    override suspend fun fetchGroupDetail(name: String): Result<ComposeGroup> = client.requestNextGroupApi {
        getGroup(name)
    }

    override suspend fun submitJoinOrExitGroup(
        name: String,
        isJoin: Boolean,
    ): Result<ComposeGroup> = client.requestWebApi(disableRedirect = true) {
        if (isJoin) {
            submitJoinGroup(name, preferenceStore.userInfo.formHash).requireNoError()
        } else {
            submitExitGroup(name, preferenceStore.userInfo.formHash).requireNoError()
        }
        client.nextGroupApi.getGroup(name)
    }
}