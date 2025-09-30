package com.xiaoyv.bangumi.shared.data.manager.coil

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable


@Immutable
@Serializable
data class PartialUrl(
    val urls: SerializeList<String> = persistentListOf(),
)
//
//class PartialUrlFetcher(
//    private val client: BgmApiClient,
//    private val partialUrl: PartialUrl,
//    private val options: Options,
//    private val imageLoader: ImageLoader,
//) : Fetcher {
//
//    override suspend fun fetch(): FetchResult? {
//        // Read the image URL.
//        val imageUrl = when {
//            // https://api.bgm.tv/v0/subjects/11/image?type=medium
//            partialUrl.username.isNotBlank() -> {
//                client.bgmJsonApi.fetchUserInfo()
//            }
//
//            partialUrl.subjectId > 0 -> {
//                client.bgmJsonApi.fesubjec()
//            }
//
//            else -> {
//                return null
//            }
//        }
//
//        // This will delegate to the internal network fetcher.
//        val data = imageLoader.components.map(imageUrl, options)
//        val output = imageLoader.components.newFetcher(data, options, imageLoader)
//        val (fetcher) = checkNotNull(output) { "no supported fetcher" }
//        return fetcher.fetch()
//    }
//
//    private suspend fun readImageUrl(response: HttpResponse): String {
//        // 假设 API 返回 JSON: { "url": "https://..." }
//        val json: Map<String, String> = response.body()
//        return json["url"] ?: error("No URL found")
//    }
//
//    class Factory(private val client: BgmApiClient) : Fetcher.Factory<PartialUrl> {
//        override fun create(data: PartialUrl, options: Options, imageLoader: ImageLoader): Fetcher {
//            return PartialUrlFetcher(client, data, options, imageLoader)
//        }
//    }
//}