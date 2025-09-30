package com.xiaoyv.bangumi.shared.data.model.request


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 新增或修改条目的内容，同名字段意义同<a href=\"#model-Subject\">Subject</a>
 *
 * @param title
 * @param description
 */
@Serializable
data class IndexBasicInfo(
    @SerialName(value = "title") val title: String? = null,
    @SerialName(value = "description") val description: String? = null,
)