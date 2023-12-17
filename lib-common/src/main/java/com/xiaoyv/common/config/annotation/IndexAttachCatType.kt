package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

/**
 * Class: [IndexAttachCatType]
 *
 * 添加到目录时，添加目标的类型
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    IndexAttachCatType.TYPE_SUBJECT,
    IndexAttachCatType.TYPE_CHARACTER,
    IndexAttachCatType.TYPE_PERSON,
    IndexAttachCatType.TYPE_EP
)
@Retention(AnnotationRetention.SOURCE)
annotation class IndexAttachCatType {
    companion object {
        const val TYPE_SUBJECT = "0"
        const val TYPE_CHARACTER = "1"
        const val TYPE_PERSON = "2"
        const val TYPE_EP = "3"
    }
}