package com.xiaoyv.bangumi.shared.data.usecase

import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.types.list.ListAlbumType
import com.xiaoyv.bangumi.shared.core.utils.runResult
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeImages
import com.xiaoyv.bangumi.shared.data.model.response.image.ComposeGallery
import com.xiaoyv.bangumi.shared.data.repository.DatabaseRepository
import com.xiaoyv.bangumi.shared.data.repository.ImageRepository
import com.xiaoyv.bangumi.shared.data.repository.IndexRepository
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.builtins.ListSerializer

/**
 * [ImageRepoUseCase]
 *
 * @since 2025/5/24
 */
class ImageRepoUseCase(
    private val imageRepository: ImageRepository,
    private val indexRepository: IndexRepository,
    private val databaseRepository: DatabaseRepository,
) {
    private val inFlightRequests = mutableMapOf<String, Deferred<SerializeList<ComposeImages>>>()
    private val mutex = Mutex()

    suspend fun fetchIndexBlogCover(id: Long): SerializeList<ComposeImages> = supervisorScope {
        val key = "index-$id"

        val deferred = mutex.withLock {
            inFlightRequests[key]?.let { return@supervisorScope it.await() }

            async {
                runResult {
                    val serializer = ListSerializer(ComposeImages.serializer())
                    val cacheData = databaseRepository.get(key, persistentListOf(), serializer)
                    if (cacheData.isNotEmpty()) cacheData.toPersistentList()
                    else {
                        val subjects = indexRepository.fetchIndexSubjects(
                            indexId = id,
                            type = SubjectType.UNKNOWN,
                            limit = 13,
                            offset = 0
                        ).getOrThrow()
                        val images = subjects.map { it.images }
                        databaseRepository.put(key, images, serializer)
                        images.toPersistentList()
                    }
                }.getOrNull() ?: persistentListOf()
            }.also { inFlightRequests[key] = it }
        }

        try {
            deferred.await()
        } finally {
            mutex.withLock { inFlightRequests.remove(key) }
        }
    }

    suspend fun fetchPictureGallery(
        id: String,
        @ListAlbumType type: Int,
    ): Result<List<ComposeGallery>> {
        return when (type) {
            ListAlbumType.PIVIX -> imageRepository.fetchPixivPictureDetail(id)
            else -> Result.success(emptyList())
        }
    }
}