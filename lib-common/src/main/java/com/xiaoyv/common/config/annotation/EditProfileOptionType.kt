package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

@StringDef(
    EditProfileOptionType.TYPE_FILE,
    EditProfileOptionType.TYPE_INPUT,
    EditProfileOptionType.TYPE_SELECTOR
)
@Retention(AnnotationRetention.SOURCE)
annotation class EditProfileOptionType {
    companion object {
        const val TYPE_INPUT = "text"
        const val TYPE_FILE = "file"
        const val TYPE_SELECTOR = "select"
    }
}