package com.xiaoyv.bangumi.ui.feature.friendly

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.FriendEntity
import com.xiaoyv.common.api.parser.impl.parserUserFriends
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.api.response.api.ApiCollectionEntity
import com.xiaoyv.common.config.annotation.SubjectType
import com.xiaoyv.common.database.BgmDatabaseManager
import com.xiaoyv.common.database.friendly.FriendlyRank
import com.xiaoyv.common.database.friendly.FriendlyStatus
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.orEmpty
import com.xiaoyv.widget.kts.sendValue
import com.xiaoyv.widget.kts.showToastCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class FriendRankImportViewModel : BaseViewModel() {
    internal val onFriendsLiveData = MutableLiveData<List<FriendEntity>?>()

    internal val onTotalProgressLiveData = MutableLiveData<Pair<Int, Int>>()

    internal val runningLiveData = MutableLiveData(false)

    internal val onCompleteLiveData = MutableLiveData<Boolean?>()

    internal val localStatsLiveData = MutableLiveData<List<FriendlyStatus>?>()

    /**
     * 正在查询中的数据
     */
    internal val onHandingLiveData = MutableLiveData<ConcurrentHashMap<String, Pair<Int, Int>>?>(
        ConcurrentHashMap()
    )

    /**
     * 并发拉取 30 组
     */
    private val fetchGroup = 30

    private val current = AtomicInteger(0)
    private val total = AtomicInteger(0)

    private val targetUserId: String
        get() = UserHelper.currentUser.id

    override fun onViewCreated() {
        queryAllFriend()
        queryLocalStatus()
    }

    private fun queryLocalStatus() {
        launchUI {
            localStatsLiveData.value = withContext(Dispatchers.IO) {
                BgmDatabaseManager.friendlyRank.getStatus()
            }
        }
    }

    private fun queryAllFriend() {
        launchUI(
            stateView = loadingViewState,
            error = {
                onFriendsLiveData.value = emptyList()
            },
            block = {
                onFriendsLiveData.value = withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.queryUserFriends(targetUserId)
                        .parserUserFriends()
                        .toMutableList()
                        .apply { add(FriendEntity(id = UserHelper.currentUser.id)) }
                }
            }
        )
    }

    /**
     * 载入朋友的收藏数据
     */
    fun startFetchFriendSubjects() {
        launchUI(
            error = {
                it.printStackTrace()

                showToastCompat(it.errorMsg)

                runningLiveData.value = false
                onCompleteLiveData.value = false
            },
            block = {
                runningLiveData.value = true
                onCompleteLiveData.value = false

                withContext(Dispatchers.IO) {
                    val entities = onFriendsLiveData.value.orEmpty()

                    // 进度初始化
                    current.set(0)
                    total.set(entities.size)
                    onTotalProgressLiveData.sendValue(current.get() to total.get())

                    check(entities.isNotEmpty()) { "你还没有朋友呢" }

                    // 先清空数据
                    BgmDatabaseManager.friendlyRank.deleteAll()

                    // 每组大小
                    val unitSize = (entities.size / fetchGroup)
                        .let { if (it == 0) 1 else it }

                    val chunked = entities.chunked(unitSize)

                    val deferredTasks = chunked.map { sub ->
                        async(Dispatchers.IO) {
                            fetchFriendSubjectsImpl(sub)
                        }
                    }.toTypedArray()

                    awaitAll(*deferredTasks)
                }

                // 重新查询本地状态
                queryLocalStatus()

                runningLiveData.value = false
                onCompleteLiveData.value = true
            }
        )
    }

    /**
     * 并发分组的每一组的逻辑实现
     */
    private suspend fun fetchFriendSubjectsImpl(subItems: List<FriendEntity>) {
        coroutineScope {
            subItems.forEach { item ->
                check(isActive)

                runCatching {
                    val userId = item.id
                    val collections = fetchUserCollections(
                        userId = userId,
                        onProgress = { progress ->
                            // 更新进度
                            val map = requireNotNull(onHandingLiveData.value)
                            map[userId] = progress
                            onHandingLiveData.sendValue(map)
                        }
                    )

                    onTotalProgressLiveData.sendValue(current.incrementAndGet() to total.get())

                    // 入库
                    saveFriendSubjectToDatabase(userId, collections)

                    // 当前用户进度完成
                    val map = requireNotNull(onHandingLiveData.value)
                    map.remove(userId)
                    onHandingLiveData.sendValue(map)
                }
            }
        }
    }

    /**
     * 循环查询某个用户全部收藏条目
     */
    private suspend fun fetchUserCollections(
        userId: String,
        onProgress: (Pair<Int, Int>) -> Unit
    ): ArrayList<ApiCollectionEntity.Data> {
        val entities = arrayListOf<ApiCollectionEntity.Data>()
        val pageSize = 100

        suspend fun request(page: Int): ApiCollectionEntity {
            val offset = (page - 1) * pageSize

            runCatching {
                return BgmApiManager.bgmJsonApi.queryUserCollect(
                    userId = userId,
                    offset = offset,
                    limit = pageSize,
                    subjectType = SubjectType.TYPE_ANIME
                )
            }

            // 请求失败的情况
            return ApiCollectionEntity(total = -1)
        }

        coroutineScope {
            var page = 1
            while (isActive) {
                var data = request(page)

                // 请求失败重试三次
                if (data.total == -1) data = request(page)
                if (data.total == -1) data = request(page)
                if (data.total == -1) data = request(page)

                val list = data.data.orEmpty()
                entities.addAll(list)
                onProgress(entities.size to data.total)
                // 没有更多了
                if (list.size < pageSize) break
                page++
            }
        }

        return entities
    }

    /**
     * 入库
     */
    @Synchronized
    private fun saveFriendSubjectToDatabase(
        userId: String,
        collections: ArrayList<ApiCollectionEntity.Data>
    ) {
        BgmDatabaseManager.friendlyRank.insertAll(
            collections.map {
                FriendlyRank(
                    master = targetUserId,
                    uid = userId,
                    comment = it.comment,
                    rate = it.rate,
                    type = it.type,
                    updatedAt = it.updatedAt,
                    volStatus = it.volStatus,
                    epStatus = it.epStatus,
                    subjectId = it.subjectId,
                    subjectType = it.subjectType,
                    subjectDate = it.subject?.date.orEmpty(),
                    subjectEps = it.subject?.eps.orEmpty(),
                    subjectVolumes = it.subject?.volumes.orEmpty(),
                    subjectScore = it.subject?.score ?: 0.0,
                    subjectCover = it.subject?.images?.large.orEmpty().optImageUrl(),
                    subjectName = it.subject?.name.orEmpty(),
                    subjectNameCn = it.subject?.nameCn.orEmpty(),
                    subjectShortSummary = it.subject?.shortSummary.orEmpty(),
                )
            }
        )
    }
}