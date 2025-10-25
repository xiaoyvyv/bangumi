package com.xiaoyv.bangumi.shared.data.repository.impl

import androidx.paging.PagingConfig
import com.xiaoyv.bangumi.shared.core.types.IndexCatWebTabType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.data.api.client.BgmApiClient
import com.xiaoyv.bangumi.shared.data.manager.app.PreferenceStore
import com.xiaoyv.bangumi.shared.data.model.request.IndexTarget
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeIndex
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.loadAllData
import com.xiaoyv.bangumi.shared.data.repository.IndexRepository

class IndexRepositoryImpl(
    private val client: BgmApiClient,
    private val pagingConfig: PagingConfig,
    private val preferenceStore: PreferenceStore,
) : IndexRepository {

    override suspend fun fetchUserCreatedIndex(username: String): Result<List<ComposeIndex>> = client.requestNextUserApi {
        loadAllData(100) { offset, limit ->
            getUserIndexes(username = username, limit = limit, offset = offset)
        }
    }

    override suspend fun fetchIndexDetail(indexId: Long): Result<ComposeIndex> = client.requestNextIndexApi {
        getIndex(indexId)
    }

    override suspend fun fetchIndexIsBookmarked(indexId: Long): Result<Boolean> = client.requestWebApi {
        // 用 EP 类型查询网页详情页数据，判断是否收藏，一般这个比较条目少，快
        fetchIndexDetail(indexId, IndexCatWebTabType.EP).select("a.btnBlue").isNotEmpty()
    }

    override suspend fun submitBookmarkOrCancelIndex(indexId: Long, bookmarked: Boolean): Result<Boolean> = client.requestJsonApi {
        if (bookmarked) {
            submitBookmarkAddIndex(indexId)
        } else {
            // TODO API有问题，暂时用WEB代替
            // submitBookmarkRemoveIndex(indexId)
            client.bgmWebApiNoRedirect.submitCollectionIndexRemove(indexId, preferenceStore.userInfo.formHash)
        }
        bookmarked
    }

    override suspend fun submitIndexAddRelated(indexId: Long, target: IndexTarget): Result<Unit> = client.requestWebApi {
        submitIndexRelatedAdd(
            indexId = indexId,
            cat = target.cat,
            addRelated = target.relateId,
            formHash = preferenceStore.userInfo.formHash
        )
    }

    override suspend fun fetchIndexSubjects(
        indexId: Long,
        type: Int,
        limit: Int,
        offset: Int,
    ): Result<List<ComposeSubject>> = client.requestJsonApi {
        fetchIndexSubjectsByIndexId(
            indexId = indexId,
            type = if (type == SubjectType.UNKNOWN) null else type,
            limit = limit,
            offset = offset
        ).result
    }
}