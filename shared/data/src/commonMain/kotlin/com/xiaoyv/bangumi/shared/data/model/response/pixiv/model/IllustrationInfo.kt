package com.xiaoyv.bangumi.shared.data.model.response.pixiv.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param id
 * @param title
 * @param type
 * @param imageUrls
 * @param caption
 * @param restrict
 * @param user
 * @param tags
 * @param tools
 * @param createDate
 * @param pageCount
 * @param width
 * @param height
 * @param sanityLevel
 * @param xRestrict
 * @param series
 * @param metaSinglePage
 * @param metaPages
 * @param totalView
 * @param totalBookmarks
 * @param isBookmarked
 * @param visible
 * @param isMuted
 * @param illustAiType
 * @param illustBookStyle
 * @param totalComments
 * @param restrictionAttributes
 */
@Serializable

data class IllustrationInfo(

    @SerialName(value = "id")
    val id: Long = 0,

    @SerialName(value = "title")
    val title: String = "",

    @SerialName(value = "type")
    val type: String = "",

    @SerialName(value = "image_urls")
    val imageUrls: ImageUrls = ImageUrls(),

    @SerialName(value = "caption")
    val caption: String = "",

    @SerialName(value = "restrict")
    val restrict: Int = 0,

    @SerialName(value = "user")
    val user: UserInfo? = null,

    @SerialName(value = "tags")
    val tags: List<IllustrationTag>? = null,

    @SerialName(value = "tools")
    val tools: List<String>? = null,

    @SerialName(value = "create_date")
    val createDate: String = "",

    @SerialName(value = "page_count")
    val pageCount: Int = 0,

    @SerialName(value = "width")
    val width: Int = 0,

    @SerialName(value = "height")
    val height: Int = 0,

    @SerialName(value = "sanity_level")
    val sanityLevel: Int = 0,

    @SerialName(value = "x_restrict")
    val xRestrict: Int = 0,

    @SerialName(value = "series")
    val series: Series? = null,

    @SerialName(value = "meta_single_page")
    val metaSinglePage: MetaSinglePage? = null,

    @SerialName(value = "meta_pages")
    val metaPages: List<MetaPage>? = null,

    @SerialName(value = "total_view")
    val totalView: Int = 0,

    @SerialName(value = "total_bookmarks")
    val totalBookmarks: Int = 0,

    @SerialName(value = "is_bookmarked")
    val isBookmarked: Boolean? = null,

    @SerialName(value = "visible")
    val visible: Boolean? = null,

    @SerialName(value = "is_muted")
    val isMuted: Boolean? = null,

    @SerialName(value = "illust_ai_type")
    val illustAiType: Int = 0,

    @SerialName(value = "illust_book_style")
    val illustBookStyle: Int = 0,

    @SerialName(value = "total_comments")
    val totalComments: Int = 0,

    @SerialName(value = "restriction_attributes")
    val restrictionAttributes: List<String>? = null

)

