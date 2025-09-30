package com.xiaoyv.bangumi.shared.data.usecase

import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.utils.awaitAll
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoWebInfo
import com.xiaoyv.bangumi.shared.data.repository.MonoRepository

/**
 * [MonoRepoUseCase]
 *
 * @since 2025/5/18
 */
class MonoRepoUseCase(private val monoRepository: MonoRepository) {

    suspend fun fetchMonoDetail(monoId: Long, @MonoType type: Int) = awaitAll(
        block1 = { monoRepository.fetchMonoDetail(monoId, type) },
        block2 = { monoRepository.fetchMonoDetailByWeb(monoId, type).recover { ComposeMonoWebInfo.Empty } }
    ).map { it.data1.copy(webInfo = it.data2) }
}