package com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 收藏数据（插画和小说共用）
 */
@Immutable
@Serializable
data class ComposePixivBookmarkData(
    @SerialName("id") val id: Long = 0,
    @SerialName("private") val private: Boolean = false
) {
    companion object {
        val Empty = ComposePixivBookmarkData()
    }
}

/**
 * Pixiv 标签信息（插画和小说通用）
 */
@Immutable
@Serializable
data class ComposePixivTag(
    @SerialName("tag") val tag: String = "",
    @SerialName("locked") val locked: Boolean = false,
    @SerialName("deletable") val deletable: Boolean = false,
    @SerialName("userId") val userId: Long = 0,
    @SerialName("userName") val userName: String = "",
    @SerialName("translation") val translation: SerializeMap<String, String> = persistentMapOf(),
    @SerialName("romaji") val romaji: String = ""
) {
    companion object {
        val Empty = ComposePixivTag()
    }
}

/**
 * 标题说明翻译
 */
@Immutable
@Serializable
data class ComposePixivTitleCaptionTranslation(
    @SerialName("workTitle") val workTitle: String = "",
    @SerialName("workCaption") val workCaption: String = ""
) {
    companion object {
        val Empty = ComposePixivTitleCaptionTranslation()
    }
}

@Immutable
@Serializable
data class ComposePixivZoneConfigItem(
    @SerialName("url") val url: String = ""
) {
    companion object {
        val Empty = ComposePixivZoneConfigItem()
    }
}

/**
 * 广告区配置
 */
@Immutable
@Serializable
data class ComposePixivZoneConfig(
    @SerialName("responsive") val responsive: ComposePixivZoneConfigItem = ComposePixivZoneConfigItem.Empty,
    @SerialName("rectangle") val rectangle: ComposePixivZoneConfigItem = ComposePixivZoneConfigItem.Empty,
    @SerialName("500x500") val size500x500: ComposePixivZoneConfigItem = ComposePixivZoneConfigItem.Empty,
    @SerialName("header") val header: ComposePixivZoneConfigItem = ComposePixivZoneConfigItem.Empty,
    @SerialName("footer") val footer: ComposePixivZoneConfigItem = ComposePixivZoneConfigItem.Empty,
    @SerialName("expandedFooter") val expandedFooter: ComposePixivZoneConfigItem = ComposePixivZoneConfigItem.Empty,
    @SerialName("logo") val logo: ComposePixivZoneConfigItem = ComposePixivZoneConfigItem.Empty,
    @SerialName("ad_logo") val adLogo: ComposePixivZoneConfigItem = ComposePixivZoneConfigItem.Empty,
    @SerialName("t_responsive_320_50") val tResponsive320x50: ComposePixivZoneConfigItem = ComposePixivZoneConfigItem.Empty,
    @SerialName("t_responsive_300_250") val tResponsive300x250: ComposePixivZoneConfigItem = ComposePixivZoneConfigItem.Empty,
    @SerialName("relatedworks") val relatedworks: ComposePixivZoneConfigItem = ComposePixivZoneConfigItem.Empty
) {
    companion object {
        val Empty = ComposePixivZoneConfig()
    }
}

@Immutable
@Serializable
data class ComposePixivOgpData(
    @SerialName("description") val description: String = "",
    @SerialName("image") val image: String = "",
    @SerialName("title") val title: String = "",
    @SerialName("type") val type: String = ""
) {
    companion object {
        val Empty = ComposePixivOgpData()
    }
}

@Immutable
@Serializable
data class ComposePixivTwitterData(
    @SerialName("description") val description: String = "",
    @SerialName("image") val image: String = "",
    @SerialName("title") val title: String = "",
    @SerialName("card") val card: String = ""
) {
    companion object {
        val Empty = ComposePixivTwitterData()
    }
}

@Immutable
@Serializable
data class ComposePixivMetaData(
    @SerialName("title") val title: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("canonical") val canonical: String = "",
    @SerialName("alternateLanguages") val alternateLanguages: SerializeMap<String, String> = persistentMapOf(),
    @SerialName("descriptionHeader") val descriptionHeader: String = "",
    @SerialName("ogp") val ogp: ComposePixivOgpData = ComposePixivOgpData.Empty,
    @SerialName("twitter") val twitter: ComposePixivTwitterData = ComposePixivTwitterData.Empty
) {
    companion object {
        val Empty = ComposePixivMetaData()
    }
}

@Immutable
@Serializable
data class ComposePixivExtraData(
    @SerialName("meta") val meta: ComposePixivMetaData = ComposePixivMetaData.Empty
) {
    companion object {
        val Empty = ComposePixivExtraData()
    }
}
