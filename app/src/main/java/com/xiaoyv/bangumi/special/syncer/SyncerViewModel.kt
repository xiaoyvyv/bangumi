package com.xiaoyv.bangumi.special.syncer

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.response.anime.AnimeBilibiliEntity
import com.xiaoyv.common.api.response.anime.AnimeSyncEntity
import com.xiaoyv.common.api.response.api.ApiCollectionEntity
import com.xiaoyv.common.api.response.douban.DouBanInterestEntity
import com.xiaoyv.common.config.annotation.BilibiliInterestType
import com.xiaoyv.common.config.annotation.BilibiliMediaType
import com.xiaoyv.common.config.annotation.DouBanInterestType
import com.xiaoyv.common.config.annotation.DouBanMediaType
import com.xiaoyv.common.database.BgmDatabaseManager
import com.xiaoyv.common.helper.SyncerHelper
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.orEmpty
import com.xiaoyv.widget.kts.sendValue
import com.xiaoyv.widget.kts.showToastCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Class: [SyncerViewModel]
 *
 * @author why
 * @since 1/23/24
 */
class SyncerViewModel : BaseViewModel() {
    internal val isBilibili = MutableLiveData(true)
    internal val isDoing = MutableLiveData(false)
    private var isBgmDataLoaded = AtomicBoolean(false)

    internal val onDatabaseInstall = MutableLiveData<Boolean>()

    /**
     * 页数大小
     */
    private var pageSize = 30

    internal val onBgmCollectProgress = MutableLiveData<Pair<Int, Int>>()

    internal val onPlatformWishCollectProgress = MutableLiveData<Pair<Int, Int>>()
    internal val onPlatformDoneCollectProgress = MutableLiveData<Pair<Int, Int>>()
    internal val onPlatformDoingCollectProgress = MutableLiveData<Pair<Int, Int>>()

    internal val onShowSyncListLiveData = UnPeekLiveData<Unit>()

    /**
     * 默认提前获取 BGM 的收藏
     */
    fun queryLocalBgmCollection() {
        launchUI(
            error = {
                it.printStackTrace()
                isBgmDataLoaded.set(false)
                showToastCompat(it.errorMsg)
            },
            block = {
                isBgmDataLoaded.set(false)
                withContext(Dispatchers.IO) {
                    val entities = arrayListOf<AnimeSyncEntity>()
                    val pageSize = 100

                    suspend fun request(page: Int): ApiCollectionEntity {
                        val offset = (page - 1) * pageSize
                        return BgmApiManager.bgmJsonApi.queryUserCollect(
                            userId = UserHelper.currentUser.id,
                            offset = offset,
                            limit = pageSize
                        )
                    }

                    var page = 1
                    while (true) {
                        val data = request(page)
                        val list = data.data.orEmpty().map { it.toSyncEntity() }
                        entities.addAll(list)
                        onBgmCollectProgress.sendValue(entities.size to data.total)
                        // 没有更多了
                        if (list.size < pageSize) break
                        page++
                    }

                    SyncerHelper.instance.cacheBgmCollect(entities)
                }
                isBgmDataLoaded.set(true)
            }
        )
    }

    /**
     * 加载平台数据
     */
    fun handleId(targetId: String) {
        launchUI(
            error = {
                it.printStackTrace()
                isDoing.value = false
                showToastCompat(it.errorMsg)
            },
            block = {
                isDoing.value = true

                if (isBilibili.value == true) {
                    handleBilibiliId(targetId)
                } else {
                    handleDouBanId(targetId)
                }

                isDoing.value = false

                onShowSyncListLiveData.value = Unit
            }
        )
    }

    /**
     * 加载哔哩哔哩平台收藏数据
     */
    private suspend fun handleBilibiliId(targetId: String) {
        withContext(Dispatchers.IO) {
            val deferred = listOf(
                BilibiliInterestType.TYPE_WISH to onPlatformWishCollectProgress,
                BilibiliInterestType.TYPE_DOING to onPlatformDoingCollectProgress,
                BilibiliInterestType.TYPE_DONE to onPlatformDoneCollectProgress,
            ).map {
                async {
                    requestBilibili(targetId, it.first, it.second)
                }
            }

            val lists = awaitAll(*deferred.toTypedArray())

            SyncerHelper.instance.cachePlatformWishCollect(lists[0])
            SyncerHelper.instance.cachePlatformDoingCollect(lists[1])
            SyncerHelper.instance.cachePlatformDoneCollect(lists[2])

            // 等待BGM同步完成
            while (isBgmDataLoaded.get().not()) {
                delay(100)
            }

            // 过滤BGM存在的数据
            SyncerHelper.instance.filterBgmNotExist()
        }
    }

    /**
     * 加载豆瓣平台收藏数据
     */
    private suspend fun handleDouBanId(targetId: String) {
        withContext(Dispatchers.IO) {
            val deferred = listOf(
                DouBanInterestType.TYPE_WISH to onPlatformWishCollectProgress,
                DouBanInterestType.TYPE_DOING to onPlatformDoingCollectProgress,
                DouBanInterestType.TYPE_DONE to onPlatformDoneCollectProgress,
            ).map {
                async {
                    requestDouBan(targetId, it.first, it.second)
                }
            }

            val lists = awaitAll(*deferred.toTypedArray())

            SyncerHelper.instance.cachePlatformWishCollect(lists[0])
            SyncerHelper.instance.cachePlatformDoingCollect(lists[1])
            SyncerHelper.instance.cachePlatformDoneCollect(lists[2])

            // 等待BGM同步完成
            while (isBgmDataLoaded.get().not()) {
                delay(100)
            }

            // 过滤BGM存在的数据
            SyncerHelper.instance.filterBgmNotExist()
        }
    }

    private suspend fun requestBilibili(
        targetId: String,
        @BilibiliInterestType followStatus: Int,
        liveData: MutableLiveData<Pair<Int, Int>>,
    ): List<AnimeSyncEntity> {
        var page = 1
        val entities = arrayListOf<AnimeSyncEntity>()
        while (true) {
            val data = BgmApiManager.bgmJsonApi.queryBilibiliUserAnime(
                uid = targetId,
                type = BilibiliMediaType.TYPE_ANIME,
                followStatus = followStatus,
                pageNumber = page,
                pageSize = 30
            ).data
            val interests = data?.list.orEmpty()
            entities.addAll(interests.map { it.toSyncEntity(BilibiliMediaType.TYPE_ANIME) })
            liveData.sendValue(entities.size to data?.total.orEmpty())
            if (interests.isEmpty()) break
            page++
        }
        return entities
    }

    private suspend fun requestDouBan(
        targetId: String,
        @DouBanInterestType status: String,
        liveData: MutableLiveData<Pair<Int, Int>>,
    ): List<AnimeSyncEntity> {
        var page = 1
        val entities = arrayListOf<AnimeSyncEntity>()
        while (true) {
            val data = BgmApiManager.bgmJsonApi.queryDouBanUserInterest(
                userId = targetId,
                status = status,
                type = null,
                start = pageSize * (page - 1),
                count = pageSize
            )
            val interests = data.interests.orEmpty()
            entities.addAll(interests.map { it.toSyncEntity() })
            liveData.sendValue(entities.size to data.total)
            if (interests.isEmpty()) break
            page++
        }
        return entities
    }

    private fun ApiCollectionEntity.Data.toSyncEntity(): AnimeSyncEntity {
        return AnimeSyncEntity(
            id = subjectId.toString(),
            name = subject?.name.orEmpty(),
            nameCn = subject?.nameCn.orEmpty()
        )
    }

    /**
     * 我的评分：★★★ 超级赞 https://movie.douban.com/subject/36093351/ 来自@豆瓣App
     */
    private fun DouBanInterestEntity.Interest.toSyncEntity(): AnimeSyncEntity {
        return AnimeSyncEntity(
            id = id.toString(),
            name = subject?.title.orEmpty(),
            nameCn = subject?.title.orEmpty(),
            comment = comment.orEmpty(),
            summary = subject?.cardSubtitle.orEmpty(),
            score = sharingText.orEmpty().count { it == '★' } * 2.0,
            image = subject?.coverUrl.orEmpty(),
            interestType = DouBanInterestType.toInterest(status.orEmpty()),
            interestText = DouBanInterestType.string(status.orEmpty(), subject?.type.orEmpty()),
            subjectType = DouBanMediaType.toSubjectType(subject?.type)
        )
    }

    private fun AnimeBilibiliEntity.Item.toSyncEntity(@BilibiliMediaType type: String): AnimeSyncEntity {
        return AnimeSyncEntity(
            id = mediaId.toString(),
            name = title.orEmpty(),
            nameCn = title.orEmpty(),
            image = cover.orEmpty(),
            summary = evaluate.orEmpty(),
            interestType = BilibiliInterestType.toInterest(followStatus),
            interestText = BilibiliInterestType.string(followStatus),
            subjectType = BilibiliMediaType.toSubjectType(type)
        )
    }

    fun installDatabase(data: Uri) {
        launchUI(
            error = {
                it.printStackTrace()
                showToastCompat(it.errorMsg)

                onDatabaseInstall.value = false
            },
            block = {
                BgmDatabaseManager.installAssetDb(data)

                onDatabaseInstall.value = true
            }
        )
    }
}