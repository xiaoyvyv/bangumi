package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

/**
 * Class: [MessageBoxType]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    MessageBoxType.TYPE_INBOX,
    MessageBoxType.TYPE_OUTBOX
)
@Retention(AnnotationRetention.SOURCE)
annotation class MessageBoxType {
    companion object {
        const val TYPE_INBOX = "inbox"
        const val TYPE_OUTBOX = "outbox"
    }
}
