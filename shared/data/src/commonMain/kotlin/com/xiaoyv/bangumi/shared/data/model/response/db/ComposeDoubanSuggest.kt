@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.model.response.db

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.db.ComposeDoubanSuggest.Target
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Class: [ComposeDoubanSuggest]
 *
 * @author why
 * @since 12/11/23
 */
@Immutable
@Serializable
data class ComposeDoubanSuggest(
    @SerialName("banned")
    val banned: String = "",
    @SerialName("cards")
    val cards: SerializeList<ComposeDoubanSuggestCard> = persistentListOf(),
    @SerialName("is_suicide")
    val isSuicide: Boolean = false,
    @SerialName("words")
    val words: SerializeList<String> = persistentListOf(),
) {


    @Immutable
    @Serializable
    data class Target(
        @SerialName("abstract")
        val `abstract`: String = "",
        @SerialName("card_subtitle")
        val cardSubtitle: String = "",
        @SerialName("controversy_reason")
        val controversyReason: String = "",
        @SerialName("cover_url")
        val coverUrl: String = "",
        @SerialName("has_linewatch")
        val hasLinewatch: Boolean = false,
        @SerialName("id")
        val id: String = "",
        @SerialName("null_rating_reason")
        val nullRatingReason: String = "",
        @SerialName("rating")
        val rating: Rating? = null,
        @SerialName("title")
        val title: String = "",
        @SerialName("uri")
        val uri: String = "",
        @SerialName("year")
        val year: String = "",
    )

    @Immutable
    @Serializable
    data class Rating(
        @SerialName("count")
        val count: Int = 0,
        @SerialName("max")
        val max: Int = 0,
        @SerialName("star_count")
        val starCount: Double = 0.0,
        @SerialName("value")
        val value: Double = 0.0,
    )

    companion object {
        val Empty = ComposeDoubanSuggest()
    }
}


@Immutable
@Serializable
data class ComposeDoubanSuggestCard(
    @SerialName("layout")
    val layout: String = "",
    @SerialName("target")
    val target: Target = Target(),
    @SerialName("target_id")
    val targetId: String = "",
    @SerialName("target_type")
    val targetType: String = "",
    @SerialName("type_name")
    val typeName: String = "",
) {
    companion object {
        val Empty = ComposeDoubanSuggestCard()
    }
}