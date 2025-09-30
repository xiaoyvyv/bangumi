package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.StringDef

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