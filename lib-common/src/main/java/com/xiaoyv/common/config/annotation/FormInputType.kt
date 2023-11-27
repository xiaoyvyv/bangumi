package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

@StringDef(
    FormInputType.TYPE_FILE,
    FormInputType.TYPE_INPUT,
    FormInputType.TYPE_SELECT,
    FormInputType.TYPE_SUBMIT,
    FormInputType.TYPE_HIDDEN,
)
@Retention(AnnotationRetention.SOURCE)
annotation class FormInputType {
    companion object {
        const val TYPE_INPUT = "text"
        const val TYPE_FILE = "file"
        const val TYPE_SELECT = "select"
        const val TYPE_SUBMIT = "submit"
        const val TYPE_HIDDEN = "hidden"
    }
}