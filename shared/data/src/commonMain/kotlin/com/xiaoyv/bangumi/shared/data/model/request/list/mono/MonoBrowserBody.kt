package com.xiaoyv.bangumi.shared.data.model.request.list.mono

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.MonoOrderByType
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.utils.toUrl
import com.xiaoyv.bangumi.shared.data.model.ui.PageUI
import io.ktor.http.parseUrlEncodedParameters
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [com.xiaoyv.bangumi.shared.data.model.request.list.subject.SubjectSearchBody]
 *
 * @author why
 * @since 2025/1/25
 */
@Immutable
@Serializable
data class MonoBrowserBody(
    @SerialName("ui") val ui: PageUI = PageUI(),

    /**
     * 列表条目请求数据类型
     */
    @field:MonoType
    @SerialName("monoType") val monoType: Int = MonoType.UNKNOWN,

    /**
     * 互斥参数
     */
    @SerialName("mutexParam") val mutexParam: BrowserMonoMutexParam = BrowserMonoMutexParam.Empty,

    /**
     * 排序 默认时间
     */
    @field:MonoOrderByType
    @SerialName("orderBy") val orderBy: String = MonoOrderByType.TYPE_DATELINE,
) {
    @Immutable
    @Serializable
    data class BrowserMonoMutexParam(
        @SerialName("type") val type: String? = null,
        @SerialName("bloodType") val bloodType: String? = null,
        @SerialName("gender") val gender: String? = null,
        @SerialName("month") val month: String? = null,
        @SerialName("day") val day: String? = null,
    ) {
        fun toFilterType(type: String) = BrowserMonoMutexParam(type = type)
        fun toFilterBloodType(bloodType: String) = BrowserMonoMutexParam(bloodType = bloodType)
        fun toFilterGender(gender: String) = BrowserMonoMutexParam(gender = gender)
        fun toFilterMonth(month: String, day: String? = null) = BrowserMonoMutexParam(month = month, day = day)

        companion object Companion {
            val Empty = BrowserMonoMutexParam()
        }
    }

    companion object Companion {
        /**
         * 解析跳转参数
         */
        fun fromUri(jumpUrl: String): MonoBrowserBody {
            return try {
                val jumpUrl = jumpUrl.toUrl()
                val parameters = jumpUrl.encodedQuery.parseUrlEncodedParameters()
                val monoType = if (jumpUrl.encodedPath.contains("character")) MonoType.CHARACTER else MonoType.PERSON
                MonoBrowserBody(
                    monoType = monoType,
                    orderBy = parameters["orderby"].orEmpty(),
                    mutexParam = BrowserMonoMutexParam(
                        type = parameters["type"],
                        bloodType = parameters["bloodtype"],
                        gender = parameters["gender"],
                        month = parameters["month"],
                        day = parameters["day"],
                    )
                )
            } catch (_: Exception) {
                Empty
            }
        }

        val Empty = MonoBrowserBody()
    }
}