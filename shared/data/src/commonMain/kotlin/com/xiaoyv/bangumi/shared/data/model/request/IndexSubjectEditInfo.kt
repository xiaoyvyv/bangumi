package com.xiaoyv.bangumi.shared.data.model.request


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 修改目录中条目的信息
 *
 * @param sort 排序条件，越小越靠前
 * @param comment
 */
@Serializable
data class IndexSubjectEditInfo(
    /* 排序条件，越小越靠前 */
    @SerialName(value = "sort") val sort: Int? = null,
    @SerialName(value = "comment") val comment: String? = null,
)
