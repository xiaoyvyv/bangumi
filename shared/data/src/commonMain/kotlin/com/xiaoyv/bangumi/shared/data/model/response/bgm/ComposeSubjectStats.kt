@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeSubjectStats(
    @SerialName("interest_type")
    val interestType: TypeData = TypeData.Empty,
    @SerialName("airdate")
    val airDate: TypeData = TypeData.Empty,
    @SerialName("relative_regdate")
    val relativeRegdate: TypeData = TypeData.Empty,
    @SerialName("regdate")
    val regDate: TypeData = TypeData.Empty,
    @SerialName("total_collects")
    val totalCollects: TypeData = TypeData.Empty,
    @SerialName("vib")
    val vib: TypeData = TypeData.Empty,
    val interestGridState: SerializeList<GridState> = persistentListOf(),
    val vibGridState: SerializeList<GridState> = persistentListOf(),
) {

    @Immutable
    @Serializable
    data class GridState(
        @SerialName("id") val id: String = "",
        @SerialName("title") val title: String = "",
        @SerialName("desc") val desc: String = "",
        @SerialName("color") val color: String = "",
    )

    @Immutable
    @Serializable
    data class TypeData(
        @SerialName("chart_root")
        val chartRoot: String = "",
        @SerialName("data")
        val dataMap: SerializeList<SerializeMap<String, Int>> = persistentListOf(),
        @SerialName("desc")
        val desc: String = "",
        @SerialName("enable_score")
        val enableScore: Boolean = false,
        @SerialName("series_set")
        val seriesSet: SerializeMap<String, String> = persistentMapOf(),
        @SerialName("title")
        val title: String = "",
    ) {
        companion object {
            val Empty = TypeData()
        }
    }

    companion object {
        val Empty = ComposeSubjectStats()
    }
}