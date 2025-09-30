package com.xiaoyv.bangumi.shared.data.api

import com.xiaoyv.bangumi.shared.core.types.AppJsonApiDsl
import com.xiaoyv.bangumi.shared.data.model.response.image.ComposeAnimePicture
import com.xiaoyv.bangumi.shared.data.model.response.image.ComposePixivImage
import com.xiaoyv.bangumi.shared.data.model.response.image.ComposePixivImageBody
import com.xiaoyv.bangumi.shared.data.model.response.image.ComposePixivResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

/**
 * [ImageApi]
 *
 * @since 2025/5/22
 */
@AppJsonApiDsl
interface ImageApi {

    /**
     * Anime-Pictures
     */
    @GET("https://api.anime-pictures.net/api/v3/posts")
    suspend fun fetchAnimePictures(
        @Query("search_tag") searchTags: String? = null,
        @Query("denied_tags") deniedTags: String? = null,
        @Query("lang") lang: String = "zh_CN",
        @Query("ldate") lDate: String = "0",
        @Query("order_by") orderBy: String = "date",
        @Query("page") page: Int = 0,
        @Query("posts_per_page") size: Int = 20,
    ): ComposeAnimePicture

    @GET("https://proxy.xiaoyv.com.cn/pixiv-ajax/ajax/search/illustrations/{tag}")
    suspend fun fetchPixivPictures(
        @Path("tag") tag: String,
        @Query("p") page: Int,
        @Query("type") type: String = "illust_and_ugoira",
        @Query("s_mode") searchMode: String = "s_tag_full",
        @Query("order") order: String = "date_d",
    ): ComposePixivResponse<ComposePixivImageBody>

    @GET("https://proxy.xiaoyv.com.cn/pixiv-ajax/ajax/illust/{id}/pages")
    suspend fun fetchPixivIllustDetail(@Path("id") id: String): ComposePixivResponse<List<ComposePixivImage>>
}