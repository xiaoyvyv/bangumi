package com.xiaoyv.bangumi.features.publish.main.business

/**
 * [PublishMainSideEffect]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class PublishMainSideEffect {
    data class OnCreatePostSuccess(val id: Long) : PublishMainSideEffect()
}