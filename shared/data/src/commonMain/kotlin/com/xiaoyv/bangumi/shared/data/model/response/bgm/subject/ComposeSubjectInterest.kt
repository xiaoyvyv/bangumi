package com.xiaoyv.bangumi.shared.data.model.response.bgm.subject

import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeDateLong
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.request.bgm.CollectionSubjectParam
import com.xiaoyv.bangumi.shared.data.model.request.bgm.CollectionSubjectProgressParam
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ComposeSubjectInterest(
    @SerialName("comment") val comment: String = "",
    @SerialName("epStatus") val epStatus: Int = 0,
    @SerialName("id") val id: Int = 0,
    @SerialName("private") val `private`: Boolean = false,
    @SerialName("rate") val rate: Int = 0,
    @SerialName("tags") val tags: SerializeList<String> = persistentListOf(),
    @SerialName("type") @field:CollectionType val type: Int = CollectionType.UNKNOWN,
    @SerialName("updatedAt") val updatedAt: SerializeDateLong = 0,
    @SerialName("volStatus") val volStatus: Int = 0,
) {
    fun toCollectionSubjectUpdate(): CollectionSubjectParam {
        return CollectionSubjectParam(
            type = type,
            rate = rate,
            comment = comment,
            `private` = `private`,
            tags = tags,
        )
    }

    fun updateFrom(update: CollectionSubjectParam): ComposeSubjectInterest {
        return copy(
            type = update.type ?: type,
            rate = update.rate ?: rate,
            comment = update.comment ?: comment,
            `private` = update.private ?: `private`,
            tags = update.tags ?: tags,
        )
    }

    fun updateFrom(update: CollectionSubjectProgressParam): ComposeSubjectInterest {
        return copy(
            epStatus = update.epStatus ?: epStatus,
            volStatus = update.volStatus ?: volStatus,
        )
    }


    companion object {
        val Empty = ComposeSubjectInterest()
    }
}
