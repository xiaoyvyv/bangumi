package com.xiaoyv.bangumi.shared.data.model.request.list.user

import androidx.compose.runtime.Immutable
import com.appmattus.crypto.Algorithm
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.ModeType
import com.xiaoyv.bangumi.shared.core.types.list.ListUserType
import com.xiaoyv.bangumi.shared.data.model.emnu.GroupMemberRole
import com.xiaoyv.bangumi.shared.data.model.ui.PageUI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [ListUserParam]
 *
 * @author why
 * @since 2025/1/25
 */
@Immutable
@Serializable
data class ListUserParam(
    @SerialName("ui") val ui: PageUI = PageUI(),

    @field:ListUserType
    @SerialName("type")
    val type: Int = ListUserType.UNKNOWN,

    @SerialName("username") val username: String = "",

    /**
     * - [ListUserType.CHARACTER_COLLECT]
     * - [ListUserType.PERSON_COLLECT]
     */
    @SerialName("characterID") val characterID: Long = 0,
    @SerialName("personID") val personID: Long = 0,

    /**
     * [ListUserType.SUBJECT_COLLECT]
     */
    @SerialName("subjectID") val subjectID: Long = 0,
    @SerialName("subjectCollectType") @field:CollectionType val subjectCollectType: Int = CollectionType.UNKNOWN,
    @SerialName("mode") @field:ModeType val mode: String = ModeType.UNKNOWN,

    /**
     * [ListUserType.GROUP_MEMBER]
     */
    @SerialName("groupName") val groupName: String = "",
    @SerialName("groupRole") val groupRole: GroupMemberRole = GroupMemberRole.Member,
) {
    val uniqueKey = Algorithm.SHA_1.hash(toString().encodeToByteArray()).toHexString()

    companion object {
        val Empty = ListUserParam()
    }
}