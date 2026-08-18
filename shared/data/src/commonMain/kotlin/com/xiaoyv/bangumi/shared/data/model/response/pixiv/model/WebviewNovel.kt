package com.xiaoyv.bangumi.shared.data.model.response.pixiv.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param id
 * @param title
 * @param seriesId
 * @param seriesTitle
 * @param seriesIsWatched
 * @param userId
 * @param coverUrl
 * @param tags
 * @param caption
 * @param cdate
 * @param rating
 * @param text
 * @param marker
 * @param illusts
 * @param images
 * @param seriesNavigation
 * @param glossaryItems
 * @param replaceableItemIds
 * @param aiType
 * @param isOriginal
 */
@Serializable

data class WebviewNovel(

    @SerialName(value = "id")
    val id: String = "",

    @SerialName(value = "title")
    val title: String = "",

    @SerialName(value = "seriesId")
    val seriesId: String = "",

    @SerialName(value = "seriesTitle")
    val seriesTitle: String = "",

    @SerialName(value = "seriesIsWatched")
    val seriesIsWatched: Boolean? = null,

    @SerialName(value = "userId")
    val userId: String = "",

    @SerialName(value = "coverUrl")
    val coverUrl: String = "",

    @SerialName(value = "tags")
    val tags: List<String>? = null,

    @SerialName(value = "caption")
    val caption: String = "",

    @SerialName(value = "cdate")
    val cdate: String = "",

    @SerialName(value = "rating")
    val rating: NovelRating? = null,

    @SerialName(value = "text")
    val text: String = "",

    @SerialName(value = "marker")
    val marker: String = "",

    @SerialName(value = "illusts")
    val illusts: List<String>? = null,

    @SerialName(value = "images")
    val images: List<String>? = null,

    @SerialName(value = "seriesNavigation")
    val seriesNavigation: NovelNavigationInfo? = null,

    @SerialName(value = "glossaryItems")
    val glossaryItems: List<String>? = null,

    @SerialName(value = "replaceableItemIds")
    val replaceableItemIds: List<String>? = null,

    @SerialName(value = "aiType")
    val aiType: Int = 0,

    @SerialName(value = "isOriginal")
    val isOriginal: Boolean? = null

)

