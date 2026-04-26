@file:OptIn(ExperimentalSerializationApi::class)

package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.MonoCastType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Immutable
@Serializable
data class ComposeMonoInfo(
    /**
     * - 条目详情页内查询相关角色才会填充
     * - 条目详情页内查询相关人物才会填充
     */
    @SerialName("mono") @JsonNames("staff", "character") val mono: ComposeMono = ComposeMono.Empty,

    /**
     * - 条目详情页内查询相关人物才会填充
     */
    @SerialName("positions") val positions: SerializeList<ComposePersonPosition> = persistentListOf(),

    /**
     * - 条目详情页内查询相关人物才会填充
     * - 角色详情页内查询出演条目才会填充
     */
    @SerialName("actors") val actors: SerializeList<ComposeMono> = persistentListOf(),

    /**
     * 条目详情页内查询相关人物才会填充
     */
    @SerialName("order") val order: Int = 0,
    @SerialName("type") @field:MonoCastType val type: Int = MonoCastType.UNKNOWN,

    /**
     * 角色详情页内查询出演条目才会填充
     */
    @SerialName("subject") val subject: ComposeSubject = ComposeSubject.Empty,

    /**
     * 人物详情页内查询出演角色才会填充
     */
    @SerialName("relations") val relations: SerializeList<ComposeCharacterSubjectRelation> = persistentListOf(),
) {
    companion object Companion {
        val Empty = ComposeMonoInfo()
    }
}

