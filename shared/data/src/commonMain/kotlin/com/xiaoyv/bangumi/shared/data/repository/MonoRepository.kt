package com.xiaoyv.bangumi.shared.data.repository

import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.ListMonoParam
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.ListPersonCastParam
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeId
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeSection
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMono
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoInfo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoWebInfo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposePersonPosition
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectDisplay
import com.xiaoyv.bangumi.shared.data.repository.datasource.MemoryPagingController

/**
 * [MonoRepository]
 *
 * @since 2025/5/18
 */
interface MonoRepository {
    fun fetchMonoListPager(param: ListMonoParam): MemoryPagingController<ComposeMonoDisplay, String>

    fun fetchPersonCastPager(param: ListPersonCastParam): MemoryPagingController<ComposeMonoInfo, Long>

    suspend fun fetchMonoListByType(param: ListMonoParam, offset: Int, limit: Int): Result<List<ComposeMonoDisplay>>

    suspend fun fetchMonoDetail(monoId: Long, @MonoType type: Int): Result<ComposeMono>

    suspend fun fetchMonoDetailByWeb(monoId: Long, @MonoType type: Int): Result<ComposeMonoWebInfo>

    suspend fun fetchMonoComments(monoId: Long, @MonoType type: Int): Result<List<ComposeReply>>

    suspend fun fetchMonoHomepage(): Result<List<ComposeSection<ComposeMonoDisplay>>>

    suspend fun fetchCharacterCasts(monoId: Long): Result<List<ComposeMonoInfo>>

    suspend fun fetchPersonCast(monoId: Long, limit: Int): Result<List<ComposeMonoInfo>>

    suspend fun fetchPersonWorks(monoId: Long, limit: Int): Result<List<ComposeSubjectDisplay>>

    suspend fun fetchPersonWorkPosition(monoId: Long): Result<List<ComposePersonPosition>>

    suspend fun submitMonoComment(
        @MonoType type: Int,
        monoId: Long,
        content: String,
        turnstile: String,
        replyTo: Long? = null
    ): Result<ComposeId>

}
