package com.xiaoyv.bangumi.shared.data.model.request.bgm

import androidx.compose.runtime.Immutable

@Immutable
data class ChallengeParam(
    val codeVerifier: String,
    val codeChallenge: String,
)
