package com.xiaoyv.bangumi.shared.data.repository

import androidx.paging.Pager
import com.xiaoyv.bangumi.shared.core.types.CollectionEpisodeType
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.data.model.request.CollectionSubjectUpdate
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEpisode
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSubject

interface CollectionRepository {

    fun fetchMyCollectionSubjectPager(
        @SubjectType subjectType: Int = 0,
        @CollectionType type: Int = 0,
        fetchEpisode: Boolean = false,
    ): Pager<Int, ComposeSubject>

    /**
     * 收藏或取消收藏 人物或角色
     */
    suspend fun submitBookmarkOrCancelMono(
        monoId: Long,
        @MonoType type: Int,
        bookmarked: Boolean,
    ): Result<Boolean>


    /**
     * 批量更新，仅支持没有收藏记录的 EP 格子，如果已经有状态了会跳过
     */
    suspend fun submitUpdateUserEpisode(
        subjectId: Long,
        episodes: List<ComposeEpisode>,
        @CollectionEpisodeType type: Int,
    ): Result<Unit>

    suspend fun submitUpdateUserSubject(
        subjectId: Long,
        update: CollectionSubjectUpdate,
    ): Result<Unit>

    suspend fun submitRemoveSubjectCollection(subjectId: Long): Result<Unit>
}