package com.xiaoyv.bangumi.features.mikan.detail.business

/**
 * [MikanDetailSideEffect]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class MikanDetailSideEffect {
    data class OnCopyText(val data: String) : MikanDetailSideEffect()
    data class OnOpenUri(val uri: String) : MikanDetailSideEffect()
}