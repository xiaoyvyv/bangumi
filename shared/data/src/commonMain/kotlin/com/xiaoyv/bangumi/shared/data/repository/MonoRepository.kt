package com.xiaoyv.bangumi.shared.data.repository

import androidx.paging.Pager
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.ListMonoParam
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.ListPersonCastParam
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeSection
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMono
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoInfo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoWebInfo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposePersonPosition
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectDisplay

/**
 * [MonoRepository]
 *
 * @since 2025/5/18
 */
interface MonoRepository {
    fun fetchMonoListPager(param: ListMonoParam): Pager<Int, ComposeMonoDisplay>

    fun fetchPersonCastPager(param: ListPersonCastParam): Pager<Int, ComposeMonoInfo>

    suspend fun fetchMonoListByType(param: ListMonoParam, offset: Int, limit: Int): Result<List<ComposeMonoDisplay>>

    suspend fun fetchMonoDetail(monoId: Long, @MonoType type: Int): Result<ComposeMono>

    suspend fun fetchMonoDetailByWeb(monoId: Long, @MonoType type: Int): Result<ComposeMonoWebInfo>

    suspend fun fetchMonoHomepage(): Result<List<ComposeSection<ComposeMonoDisplay>>>

    suspend fun fetchCharacterCasts(monoId: Long): Result<List<ComposeMonoInfo>>

    suspend fun fetchPersonCast(monoId: Long, limit: Int): Result<List<ComposeMonoInfo>>

    suspend fun fetchPersonWorks(monoId: Long, limit: Int): Result<List<ComposeSubjectDisplay>>

    suspend fun fetchPersonWorkPosition(monoId: Long): Result<List<ComposePersonPosition>>
}