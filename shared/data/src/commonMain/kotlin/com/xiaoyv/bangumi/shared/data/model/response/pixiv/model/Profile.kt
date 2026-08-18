package com.xiaoyv.bangumi.shared.data.model.response.pixiv.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param webpage
 * @param gender
 * @param birth
 * @param birthDay
 * @param birthYear
 * @param region
 * @param addressId
 * @param countryCode
 * @param job
 * @param jobId
 * @param totalFollowUsers
 * @param totalMypixivUsers
 * @param totalIllusts
 * @param totalManga
 * @param totalNovels
 * @param totalIllustBookmarksPublic
 * @param totalIllustSeries
 * @param totalNovelSeries
 * @param backgroundImageUrl
 * @param twitterAccount
 * @param twitterUrl
 * @param pawooUrl
 * @param isPremium
 * @param isUsingCustomProfileImage
 */
@Serializable

data class Profile(

    @SerialName(value = "webpage")
    val webpage: String = "",

    @SerialName(value = "gender")
    val gender: String = "",

    @SerialName(value = "birth")
    val birth: String = "",

    @SerialName(value = "birth_day")
    val birthDay: String = "",

    @SerialName(value = "birth_year")
    val birthYear: Int = 0,

    @SerialName(value = "region")
    val region: String = "",

    @SerialName(value = "address_id")
    val addressId: Int = 0,

    @SerialName(value = "country_code")
    val countryCode: String = "",

    @SerialName(value = "job")
    val job: String = "",

    @SerialName(value = "job_id")
    val jobId: Int = 0,

    @SerialName(value = "total_follow_users")
    val totalFollowUsers: Int = 0,

    @SerialName(value = "total_mypixiv_users")
    val totalMypixivUsers: Int = 0,

    @SerialName(value = "total_illusts")
    val totalIllusts: Int = 0,

    @SerialName(value = "total_manga")
    val totalManga: Int = 0,

    @SerialName(value = "total_novels")
    val totalNovels: Int = 0,

    @SerialName(value = "total_illust_bookmarks_public")
    val totalIllustBookmarksPublic: Int = 0,

    @SerialName(value = "total_illust_series")
    val totalIllustSeries: Int = 0,

    @SerialName(value = "total_novel_series")
    val totalNovelSeries: Int = 0,

    @SerialName(value = "background_image_url")
    val backgroundImageUrl: String = "",

    @SerialName(value = "twitter_account")
    val twitterAccount: String = "",

    @SerialName(value = "twitter_url")
    val twitterUrl: String = "",

    @SerialName(value = "pawoo_url")
    val pawooUrl: String = "",

    @SerialName(value = "is_premium")
    val isPremium: Boolean? = null,

    @SerialName(value = "is_using_custom_profile_image")
    val isUsingCustomProfileImage: Boolean? = null

)

