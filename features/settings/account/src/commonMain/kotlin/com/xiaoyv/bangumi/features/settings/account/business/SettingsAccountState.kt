package com.xiaoyv.bangumi.features.settings.account.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.EditInfoType
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUserEdit

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
    val items: Map<String, String> = emptyMap(),
    val networkItems: Map<String, String> = emptyMap(),
) {
    fun toComposeEditInfo() = ComposeUserEdit(
        avatar = items[EditInfoType.TYPE_AVATAR].orEmpty(),
        nickname = items[EditInfoType.TYPE_NICKNAME].orEmpty(),
        sign = items[EditInfoType.TYPE_SIGN].orEmpty(),
        timezone = items[EditInfoType.TYPE_TIMEZONE].orEmpty(),
        site = items[EditInfoType.TYPE_SITE].orEmpty(),
        intro = items[EditInfoType.TYPE_INTRO].orEmpty(),
        internetPsn = networkItems[EditInfoType.TYPE_INTERNET_PSN].orEmpty(),
        internetXbox = networkItems[EditInfoType.TYPE_INTERNET_XBOX].orEmpty(),
        internetSteam = networkItems[EditInfoType.TYPE_INTERNET_STEAM].orEmpty(),
        internetPixi = networkItems[EditInfoType.TYPE_INTERNET_PIXI].orEmpty(),
        internetGithub = networkItems[EditInfoType.TYPE_INTERNET_GITHUB].orEmpty(),
        internetTwitter = networkItems[EditInfoType.TYPE_INTERNET_TWITTER].orEmpty(),
        internetIns = networkItems[EditInfoType.TYPE_INTERNET_INS].orEmpty()
    )

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
