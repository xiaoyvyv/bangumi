package com.xiaoyv.bangumi.shared.data.repository

import com.xiaoyv.bangumi.shared.data.model.response.bgm.terminal.ComposeTerminalMessage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.terminal.ComposeTerminalPersonality
import com.xiaoyv.bangumi.shared.data.model.response.bgm.terminal.ComposeTerminalSpeech

interface TerminalRepository {
    suspend fun fetchPersonalities(creator: String? = null): Result<List<ComposeTerminalPersonality>>

    suspend fun fetchSpeeches(curPsn: Long, all: Boolean? = null): Result<List<ComposeTerminalSpeech>>

    suspend fun submitCreatePersonality(name: String): Result<ComposeTerminalMessage>

    suspend fun submitCreateSpeech(speech: String, curPsn: Long): Result<ComposeTerminalMessage>
}
