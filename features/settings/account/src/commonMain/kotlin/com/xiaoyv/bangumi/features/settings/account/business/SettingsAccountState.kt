package com.xiaoyv.bangumi.features.settings.account.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeMap
import kotlinx.collections.immutable.persistentMapOf

/**
 * [SettingsAccountState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class SettingsAccountState(
    val avatarBytes: ByteArray = byteArrayOf(),
    val loading: Boolean = false,
    val avatar: String = "",
    val items: SerializeMap<String, String> = persistentMapOf(),
    val networkItems: SerializeMap<String, String> = persistentMapOf(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SettingsAccountState

        if (loading != other.loading) return false
        if (!avatarBytes.contentEquals(other.avatarBytes)) return false
        if (avatar != other.avatar) return false
        if (items != other.items) return false
        if (networkItems != other.networkItems) return false

        return true
    }

    override fun hashCode(): Int {
        var result = loading.hashCode()
        result = 31 * result + avatarBytes.contentHashCode()
        result = 31 * result + avatar.hashCode()
        result = 31 * result + items.hashCode()
        result = 31 * result + networkItems.hashCode()
        return result
    }
}
