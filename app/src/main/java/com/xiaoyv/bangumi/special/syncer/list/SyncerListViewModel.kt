package com.xiaoyv.bangumi.special.syncer.list

import androidx.lifecycle.MutableLiveData
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.request.SubjectCollectParam
import com.xiaoyv.common.api.request.SyncNameParam
import com.xiaoyv.common.api.response.anime.AnimeSyncEntity
import com.xiaoyv.common.config.annotation.InterestType
import com.xiaoyv.common.database.BgmDatabaseManager
import com.xiaoyv.common.helper.SyncerHelper
import com.xiaoyv.widget.kts.sendValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.roundToInt

/**
 * Class: [SyncerListViewModel]
 *
 * @author why
 * @since 1/26/24
 */
class SyncerListViewModel : BaseListViewModel<AnimeSyncEntity>() {
    internal val onSyncProgress = MutableLiveData<Pair<Int, Int>>()
    internal val onSyncSubject = MutableLiveData<String>()
    internal val onSyncFinish = UnPeekLiveData<Boolean>()

    private val useLocal = true

    override suspend fun onRequestListImpl(): List<AnimeSyncEntity> {
        val fetchData = SyncerHelper.instance.fetchData()
        return if (useLocal) filterByLocal(fetchData) else filterByRemote(fetchData)
    }

    private suspend fun filterByRemote(fetchData: List<AnimeSyncEntity>): List<AnimeSyncEntity> {
        return BgmApiManager.bgmJsonApi.filterBgmItems(fetchData.map {
            SyncNameParam(id = it.id, name = it.name)
        }).data.orEmpty()
    }

    private suspend fun filterByLocal(fetchData: List<AnimeSyncEntity>): List<AnimeSyncEntity> {
        val newList = arrayListOf<AnimeSyncEntity>()
        coroutineScope {
            val deferred = fetchData.chunked(300).map { chunked ->
                async {
                    chunked.forEach { item ->
                        if (item.name.isNotBlank()) {
                            val items = BgmDatabaseManager.subject.getByName(item.name)
                            if (items.isNotEmpty()) {
                                item.subject = items
                                newList.add(item)
                            }
                        }
                    }
                }
            }
            awaitAll(*deferred.toTypedArray())
        }
        return newList
    }

    /**
     * 开始同步
     */
    fun startSync() {
        val syncEntities = onListLiveData.value.orEmpty()
        val total = syncEntities.size
        val progress = AtomicInteger(0)

        launchUI(
            error = {
                it.printStackTrace()

                val pair = progress.incrementAndGet() to total
                onSyncProgress.value = pair
                checkProgress(pair)
            },
            block = {
                onSyncProgress.value = (0 to total)

                // 分组并发
                val group = 10
                val chunkedCount = (total / group).let { if (it == 0) total else it }
                val deferred = syncEntities.chunked(chunkedCount).map { items ->
                    async(Dispatchers.IO) {
                        items.forEach { item ->

                            // 同步条目
                            item.subject.forEach { subject ->
                                runCatching {
                                    BgmApiManager.bgmJsonApi.postSubjectCollection(
                                        mediaId = subject.id.toString(),
                                        param = SubjectCollectParam(
                                            comment = item.comment,
                                            rate = item.score.roundToInt(),
                                            type = InterestType.toIntType(item.interestType)
                                        )
                                    ).let {
                                        require(it.isSuccessful) {
                                            "同步失败：" + (it.body() ?: it.errorBody())?.string()
                                        }
                                    }
                                }.onFailure {
                                    it.printStackTrace()
                                }
                            }

                            val pair = progress.incrementAndGet() to total
                            onSyncProgress.sendValue(pair)
                            checkProgress(pair)
                        }
                    }
                }

                awaitAll(*deferred.toTypedArray())
            }
        )
    }

    /**
     * 检查是否完成了同步
     */
    private fun checkProgress(pair: Pair<Int, Int>) {
        if (pair.first == pair.second && pair.first != 0) {
            onSyncFinish.postValue(true)
        }
    }
}