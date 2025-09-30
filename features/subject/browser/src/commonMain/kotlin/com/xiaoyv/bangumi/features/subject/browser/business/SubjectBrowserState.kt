package com.xiaoyv.bangumi.features.subject.browser.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.ListSubjectParam
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [SubjectBrowserState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
@Serializable
data class SubjectBrowserState(
    @SerialName("title") val title: String = "",
    @SerialName("param") val param: ListSubjectParam = ListSubjectParam.Empty,
)
