package com.xiaoyv.bangumi.shared.data.repository.impl

import com.xiaoyv.bangumi.shared.data.api.client.ApiClient
import com.xiaoyv.bangumi.shared.data.model.response.bgm.terminal.ComposeTerminalMessage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.terminal.ComposeTerminalPersonality
import com.xiaoyv.bangumi.shared.data.model.response.bgm.terminal.ComposeTerminalSpeech
import com.xiaoyv.bangumi.shared.data.repository.TerminalRepository

class TerminalRepositoryImpl(
    private val client: ApiClient,
) : TerminalRepository {

    override suspend fun fetchPersonalities(creator: String?): Result<List<ComposeTerminalPersonality>> =
        client.requestNextTerminalApi {
            getTerminalPersonalities(creator = creator.takeIf { !it.isNullOrBlank() })
        }

    override suspend fun fetchSpeeches(
        curPsn: Long,
        all: Boolean?,
    ): Result<List<ComposeTerminalSpeech>> = client.requestNextTerminalApi {
        getTerminalSpeeches(curPsn = curPsn, all = all)
    }

    override suspend fun submitCreatePersonality(name: String): Result<ComposeTerminalMessage> =
        client.requestNextTerminalApi {
            createTerminalPersonality(name = name)
        }

    override suspend fun submitCreateSpeech(
        speech: String,
        curPsn: Long,
    ): Result<ComposeTerminalMessage> = client.requestNextTerminalApi {
        createTerminalSpeech(speech = speech, curPsn = curPsn)
    }
}
