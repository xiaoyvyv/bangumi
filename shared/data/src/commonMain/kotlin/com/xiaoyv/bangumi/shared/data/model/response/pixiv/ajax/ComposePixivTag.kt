package com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposePixivTagCandidate(
    @SerialName("tag_name") val tagName: String = "",
    @SerialName("illust_count") val illustCount: Long = 0,
    @SerialName("total_count") val totalCount: Long = 0,
    @SerialName("suggest_type") val suggestType: String = ""
) {
    companion object {
        val Empty = ComposePixivTagCandidate()
    }
}

@Immutable
@Serializable
data class ComposePixivTagSuggestBody(
    @SerialName("candidates") val candidates: SerializeList<ComposePixivTagCandidate> = persistentListOf()
) {
    companion object {
        val Empty = ComposePixivTagSuggestBody()
    }
}

@Immutable
@Serializable
data class ComposePixivTagTranslation(
    @SerialName("tag") val tag: String = "",
    @SerialName("abstract") val abstract: String = "",
    @SerialName("url") val url: String = ""
) {
    companion object {
        val Empty = ComposePixivTagTranslation()
    }
}

@Immutable
@Serializable
data class ComposePixivTagInfoBody(
    @SerialName("tag") val tag: String = "",
    @SerialName("abstract") val abstract: String = "",
    @SerialName("thumbnail") val thumbnail: String = "",
    @SerialName("en") val en: ComposePixivTagTranslation = ComposePixivTagTranslation.Empty,
    @SerialName("en_new") val enNew: ComposePixivTagTranslation = ComposePixivTagTranslation.Empty,
    @SerialName("ja") val ja: ComposePixivTagTranslation = ComposePixivTagTranslation.Empty,
    @SerialName("ja_new") val jaNew: ComposePixivTagTranslation = ComposePixivTagTranslation.Empty,
    @SerialName("is_view_lead_wire") val isViewLeadWire: Boolean = false
) {
    companion object {
        val Empty = ComposePixivTagInfoBody()
    }
}

@Immutable
@Serializable
data class ComposePixivAddTagBody(
    @SerialName("success") val success: Boolean = false,
    @SerialName("message") val message: String = ""
) {
    companion object {
        val Empty = ComposePixivAddTagBody()
    }
}

@Immutable
@Serializable
data class ComposePixivSearchSuggestionBody(
    @SerialName("popularTags") val popularTags: SerializeList<ComposePixivTagCandidate> = persistentListOf(),
    @SerialName("recommendTags") val recommendTags: SerializeList<ComposePixivTagCandidate> = persistentListOf()
) {
    companion object {
        val Empty = ComposePixivSearchSuggestionBody()
    }
}

@Immutable
@Serializable
data class ComposePixivTagSearchSuggestBody(
    @SerialName("candidates") val candidates: SerializeList<ComposePixivTagCandidate> = persistentListOf()
) {
    companion object {
        val Empty = ComposePixivTagSearchSuggestBody()
    }
}
