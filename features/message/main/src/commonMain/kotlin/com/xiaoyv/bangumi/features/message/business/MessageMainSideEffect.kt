package com.xiaoyv.bangumi.features.message.business

import com.xiaoyv.bangumi.shared.core.types.MessageBoxType

/**
 * [MessageMainSideEffect]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class MessageMainSideEffect {
    data class OnRefreshList(@field:MessageBoxType val type: String) : MessageMainSideEffect()
}