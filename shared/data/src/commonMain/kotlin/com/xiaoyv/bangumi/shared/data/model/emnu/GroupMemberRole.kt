package com.xiaoyv.bangumi.shared.data.model.emnu


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 小组成员角色   - -2 = 访客   - -1 = 游客   - 0 = 小组成员   - 1 = 小组长   - 2 = 小组管理员   - 3 = 禁言成员
 *
 * Values: Visitor,Guest,Member,Creator,Moderator,Blocked
 */
@Serializable
enum class GroupMemberRole(val value: Int) {

    @SerialName(value = "-2")
    Visitor(-2),

    @SerialName(value = "-1")
    Guest(-1),

    @SerialName(value = "0")
    Member(0),

    @SerialName(value = "1")
    Creator(1),

    @SerialName(value = "2")
    Moderator(2),

    @SerialName(value = "3")
    Blocked(3);

    /**
     * Override [toString()] to avoid using the enum variable name as the value, and instead use
     * the actual value defined in the API spec file.
     *
     * This solves a problem when the variable name and its value are different, and ensures that
     * the client sends the correct enum values to the server always.
     */
    override fun toString(): String = value.toString()

    companion object {
        /**
         * Converts the provided [data] to a [String] on success, null otherwise.
         */
        fun encode(data: Any?): String? = if (data is GroupMemberRole) "$data" else null

        /**
         * Returns a valid [GroupMemberRole] for [data], null otherwise.
         */
        fun decode(data: Any?): GroupMemberRole? = data?.let {
            val normalizedData = "$it".lowercase()
            entries.firstOrNull { value ->
                it == value || normalizedData == "$value".lowercase()
            }
        }
    }
}

