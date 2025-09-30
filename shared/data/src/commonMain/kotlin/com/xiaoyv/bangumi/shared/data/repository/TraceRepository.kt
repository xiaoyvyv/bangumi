package com.xiaoyv.bangumi.shared.data.repository

import com.xiaoyv.bangumi.shared.data.model.response.trace.ComposeTraceCharacter
import com.xiaoyv.bangumi.shared.data.model.response.trace.ComposeTraceMoe
import com.xiaoyv.bangumi.shared.data.model.response.trace.ComposeTraceName

/**
 * [TraceRepository]
 *
 * @since 2025/5/15
 */
interface TraceRepository {
    suspend fun fetchAniTitleMapByEmbed(): Result<List<ComposeTraceName>>

    suspend fun fetchAniTitleMapByJsdelivr(): Result<List<ComposeTraceName>>

    suspend fun fetchAniTitleMapByGithub(): Result<List<ComposeTraceName>>

    suspend fun fetchSubjectInfoFromImage(byteArray: ByteArray): Result<ComposeTraceMoe>

    suspend fun fetchCharacterInfoFromImage(
        byteArray: ByteArray,
        model: String,
        aiDetect: Boolean = false,
        showMulti: Boolean = true,
    ): Result<ComposeTraceCharacter>
}