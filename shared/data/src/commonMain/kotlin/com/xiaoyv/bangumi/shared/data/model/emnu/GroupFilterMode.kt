package com.xiaoyv.bangumi.shared.data.model.emnu


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 小组过滤模式   - all = 所有小组   - joined = 我加入的小组   - managed = 我管理的小组
 *
 * Values: All,Joined,Managed
 */
@Serializable
enum class GroupFilterMode(val value: String) {

    @SerialName(value = "all")
    All("all"),

    @SerialName(value = "joined")
    Joined("joined"),

    @SerialName(value = "managed")
    Managed("managed");

    /**
     * Override [toString()] to avoid using the enum variable name as the value, and instead use
     * the actual value defined in the API spec file.
     *
     * This solves a problem when the variable name and its value are different, and ensures that
     * the client sends the correct enum values to the server always.
     */
    override fun toString(): String = value

    companion object {
        /**
         * Converts the provided [data] to a [String] on success, null otherwise.
         */
        fun encode(data: Any?): String? = if (data is GroupFilterMode) "$data" else null

        /**
         * Returns a valid [GroupFilterMode] for [data], null otherwise.
         */
        fun decode(data: Any?): GroupFilterMode? = data?.let {
            val normalizedData = "$it".lowercase()
            entries.firstOrNull { value ->
                it == value || normalizedData == "$value".lowercase()
            }
        }
    }
}

