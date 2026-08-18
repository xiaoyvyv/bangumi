package com.xiaoyv.bangumi.shared.data.model.response.pixiv.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param id
 * @param title
 * @param caption
 * @param restrict
 * @param xRestrict
 * @param isOriginal
 * @param imageUrls
 * @param createDate
 * @param tags
 * @param pageCount
 * @param textLength
 * @param user
 * @param series
 * @param isBookmarked
 * @param totalBookmarks
 * @param totalView
 * @param visible
 * @param totalComments
 * @param isMuted
 * @param isMypixivOnly
 * @param isXRestricted
 * @param novelAiType
 * @param commentAccessControl
 */
@Serializable

data class NovelInfo(

    @SerialName(value = "id")
    val id: Long = 0,

    @SerialName(value = "title")
    val title: String = "",

    @SerialName(value = "caption")
    val caption: String = "",

    @SerialName(value = "restrict")
    val restrict: Int = 0,

    @SerialName(value = "x_restrict")
    val xRestrict: Int = 0,

    @SerialName(value = "is_original")
    val isOriginal: Boolean? = null,

    @SerialName(value = "image_urls")
    val imageUrls: ImageUrls? = null,

    @SerialName(value = "create_date")
    val createDate: String = "",

    @SerialName(value = "tags")
    val tags: List<NovelTag>? = null,

    @SerialName(value = "page_count")
    val pageCount: Int = 0,

    @SerialName(value = "text_length")
    val textLength: Int = 0,

    @SerialName(value = "user")
    val user: UserInfo? = null,

    @SerialName(value = "series")
    val series: Series? = null,

    @SerialName(value = "is_bookmarked")
    val isBookmarked: Boolean? = null,

    @SerialName(value = "total_bookmarks")
    val totalBookmarks: Int = 0,

    @SerialName(value = "total_view")
    val totalView: Int = 0,

    @SerialName(value = "visible")
    val visible: Boolean? = null,

    @SerialName(value = "total_comments")
    val totalComments: Int = 0,

    @SerialName(value = "is_muted")
    val isMuted: Boolean? = null,

    @SerialName(value = "is_mypixiv_only")
    val isMypixivOnly: Boolean? = null,

    @SerialName(value = "is_x_restricted")
    val isXRestricted: Boolean? = null,

    @SerialName(value = "novel_ai_type")
    val novelAiType: Int = 0,

    @SerialName(value = "comment_access_control")
    val commentAccessControl: Int = 0

)

