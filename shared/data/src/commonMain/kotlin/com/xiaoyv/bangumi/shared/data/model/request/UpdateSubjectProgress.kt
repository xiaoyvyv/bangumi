package com.xiaoyv.bangumi.shared.data.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param epStatus 书籍条目章节进度
 * @param volStatus 书籍条目卷数进度
 */
@Serializable
data class UpdateSubjectProgress(

    /* 书籍条目章节进度 */
    @SerialName(value = "epStatus") val epStatus: Int? = null,

    /* 书籍条目卷数进度 */
    @SerialName(value = "volStatus") val volStatus: Int? = null,
)

