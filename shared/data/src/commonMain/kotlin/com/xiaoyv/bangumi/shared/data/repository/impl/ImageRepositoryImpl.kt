package com.xiaoyv.bangumi.shared.data.repository.impl

import androidx.compose.ui.graphics.Color
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.xiaoyv.bangumi.shared.core.types.list.ListAlbumType
import com.xiaoyv.bangumi.shared.core.utils.parseHtmlHexColor
import com.xiaoyv.bangumi.shared.core.utils.pixivNormalUrl
import com.xiaoyv.bangumi.shared.core.utils.pixivOriginalUrl
import com.xiaoyv.bangumi.shared.core.utils.runResult
import com.xiaoyv.bangumi.shared.core.utils.toApiOffset
import com.xiaoyv.bangumi.shared.data.api.client.BgmApiClient
import com.xiaoyv.bangumi.shared.data.model.request.list.album.ListAlbumParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMono
import com.xiaoyv.bangumi.shared.data.model.response.image.ComposeGallery
import com.xiaoyv.bangumi.shared.data.parser.bgm.SubjectParser
import com.xiaoyv.bangumi.shared.data.repository.ImageRepository
import com.xiaoyv.bangumi.shared.data.repository.datasource.createNetworkPageLimitPagingPager
import com.xiaoyv.bangumi.shared.data.repository.datasource.createPagingConfig
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlin.math.roundToInt

/**
 * [ImageRepositoryImpl]
 *
 * @since 2025/5/22
 */
class ImageRepositoryImpl(
    private val client: BgmApiClient,
    private val pagingConfig: PagingConfig,
    private val subjectParser: SubjectParser,
) : ImageRepository {

    override fun fetchAlbumPager(param: ListAlbumParam): Pager<Int, ComposeGallery> {
        return createNetworkPageLimitPagingPager(
            pagingConfig = pagingConfig,
            keySelector = { it.id },
            onLoadData = { page ->
                fetchAlbumList(param, page, pagingConfig.pageSize).getOrThrow()
            }
        )
    }


    override fun fetchAnimePictures(
        searchTags: String?,
        deniedTags: String?,
    ): Pager<Int, ComposeGallery> {
        return createNetworkPageLimitPagingPager(
            pagingConfig = pagingConfig,
            keySelector = { it.id },
            onLoadData = { page ->
                val picture = client.imageApi.fetchAnimePictures(
                    searchTags = searchTags,
                    deniedTags = deniedTags,
                    page = page,
                    size = pagingConfig.pageSize
                )
                picture.posts.orEmpty().map {
                    ComposeGallery(
                        id = it.id,
                        type = ListAlbumType.ANIME_PICTURES,
                        image = it.url,
                        original = it.largeUrl,
                        width = it.width,
                        height = it.height,
                        size = it.size,
                        color = it.color.orEmpty()
                    )
                }
            }
        )
    }

    override fun fetchPixivPictures(tag: String): Pager<Int, ComposeGallery> {
        return createNetworkPageLimitPagingPager(
            pagingConfig = createPagingConfig(60),
            keySelector = { it.id },
            onLoadData = { page ->
                val picture = client.imageApi.fetchPixivPictures(
                    tag = tag,
                    page = page,
                )
                picture.body?.illust?.data.orEmpty().map {
                    val pixivOriginalUrl = it.url
                        .orEmpty()
                        .ifBlank { it.urls?.regular }
                        .orEmpty()
                        .pixivOriginalUrl()

                    val pixivNormalUrl = pixivOriginalUrl.pixivNormalUrl()

                    ComposeGallery(
                        id = it.id.orEmpty(),
                        type = ListAlbumType.PIVIX,
                        image = pixivNormalUrl,
                        original = pixivOriginalUrl,
                        width = it.width,
                        height = it.height,
                        count = it.pageCount
                    )
                }
            }
        )
    }

    override suspend fun fetchAlbumList(param: ListAlbumParam, page: Int, size: Int): Result<List<ComposeGallery>> {
        return when (param.type) {
            ListAlbumType.CHARACTER_ALBUM -> client.requestWebApi {
                with(subjectParser) {
                    fetchCharacterAlbum(param.characterId, page)
                        .fetchCharacterAlbumCoverted()
                        .map { it.copy(type = param.type) }
                }
            }

            ListAlbumType.SUBJECT_PREVIEW -> client.requestDouBanApi {
                client.dbApi
                    .queryDouBanPhotoList(param.doubanId, param.doubanType, page.toApiOffset(size), size)
                    .copy(doubanMediaId = param.doubanId)
                    .photos.map {
                        val hexColor = parseHtmlHexColor(it.image.primaryColor.orEmpty()) ?: Color.LightGray
                        val largeImage = it.displayLargeImage

                        ComposeGallery(
                            id = largeImage.url.orEmpty(),
                            type = param.type,
                            width = largeImage.width,
                            height = largeImage.height,
                            image = largeImage.url.orEmpty(),
                            original = largeImage.url.orEmpty(),
                            color = listOf(
                                (hexColor.red * 255).roundToInt().coerceIn(0, 255),
                                (hexColor.green * 255).roundToInt().coerceIn(0, 255),
                                (hexColor.blue * 255).roundToInt().coerceIn(0, 255),
                            )
                        )
                    }
            }

            else -> error("not support")
        }
    }


    override suspend fun fetchPixivPictureDetail(id: String): Result<List<ComposeGallery>> =
        runResult {
            client.imageApi.fetchPixivIllustDetail(id).body.orEmpty().let {
                it.map { item ->
                    ComposeGallery(
                        id = item.id.orEmpty(),
                        type = ListAlbumType.PIVIX,
                        image = item.urls?.regular.orEmpty(),
                        original = item.urls?.original.orEmpty(),
                        width = item.width,
                        height = item.height,
                        count = it.size
                    )
                }
            }
        }

    override suspend fun fetchAnimePictureTag(data: ComposeMono): Result<List<String>> =
        runResult {
            val names = arrayListOf<String>()
            names.add(data.name)

            val nameInfo = data.infobox.find { it.key == "别名" }?.value
            if (nameInfo !is JsonArray) {
                val string = nameInfo?.jsonPrimitive?.contentOrNull.orEmpty()
                if (string.isNotBlank()) names.add(string)
            } else {
                names.addAll(nameInfo.mapNotNull {
                    (it as? JsonObject)?.getValue("v")?.jsonPrimitive?.contentOrNull
                })
            }

            // 原始的 Tag
            names
                .filterNot { it.isBlank() || it.matches(Regex("^[\\u4e00-\\u9fa5]+$")) }
                .distinct()
        }
}