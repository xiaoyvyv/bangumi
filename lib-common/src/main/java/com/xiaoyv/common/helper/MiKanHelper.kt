package com.xiaoyv.common.helper

import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ResourceUtils
import com.xiaoyv.blueprint.kts.launchProcess
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.fromJson
import com.xiaoyv.widget.kts.orEmpty
import com.xiaoyv.widget.kts.workDirOf
import kotlinx.coroutines.Dispatchers
import org.jsoup.Jsoup

/**
 * Class: [MiKanHelper]
 *
 * @author why
 * @since 3/20/24
 */
object MiKanHelper {
    private val idMap = hashMapOf<String, String>()

    private val mikanMapFile by lazy {
        FileUtils.getFileByPath(workDirOf("mikan") + "/mikan.json")
    }

    /**
     * 预加载 Mikan ID 映射同步功能
     */
    fun preload() {
        launchProcess(Dispatchers.IO) {
            if (!FileUtils.isFileExists(mikanMapFile)) {
                val text = ResourceUtils.readAssets2String("config/mikan/mikan.json")
                FileIOUtils.writeFileFromString(mikanMapFile, text)
            }

            // 加载存在的映射
            val string = mikanMapFile.readText()
            idMap.putAll(string.fromJson<Map<String, String>>().orEmpty())

            // 存在的最大 ID
            val localMaxId = idMap.maxOf { it.key.toLongOrNull().orEmpty() }

            debugLog { "Mikan, local max id = $localMaxId" }

            // 同步
            checkOrSyncMikanId(localMaxId)
        }
    }

    private suspend fun checkOrSyncMikanId(localMaxId: Long) {
        val mikanId = BgmApiManager.bgmWebApi.queryMikanHome()
            .select(".m-week-square a")
            .maxOf {
                it.attr("href")
                    .substringAfterLast("/")
                    .toLongOrNull()
                    .orEmpty()
            }

        debugLog { "Mikan, remote max id = $mikanId" }

        if (localMaxId >= mikanId) {
            debugLog { "Mikan, 无需同步!" }
            return
        }

        // 待同步的集合
        val needSyncIds = arrayListOf<Long>()
        for (id in localMaxId..mikanId) {
            needSyncIds.add(id)
        }

        debugLog { "Mikan, 待更新数据: ${needSyncIds.toJson()}" }

        // 开始同步
        needSyncIds.chunked(100).forEach {
            launchProcess(Dispatchers.IO) {
                it.forEach { id ->
                    syncMiKan(id)
                }
            }
        }
    }

    /**
     * 同步 ID 映射，并保存本地
     */
    private suspend fun syncMiKan(id: Long) {
        runCatching {
            val response = BgmApiManager.bgmWebApi.queryMikanDetail(id.toString())

            require(response.isSuccessful) { "Mikan 同步错误码: ${response.code()}" }

            val document = Jsoup.parse(response.body()?.string().orEmpty())
            val links = document.select("a.w-other-c")
            val element = links.find { it.text().contains("bgm.tv/subject") }
            requireNotNull(element) { "MiKan: $id, Bgm id is empty!" }
            val bgmId = element.text().substringAfterLast("/").trim()
            idMap[id.toString()] = bgmId
            val sortedMap = idMap.toSortedMap { p0, p1 ->
                p0.toInt() - p1.toInt()
            }

            debugLog { "Mikan, 同步成功: $id" }

            FileIOUtils.writeFileFromString(mikanMapFile, GsonUtils.toJson(sortedMap))
        }.onFailure {
            debugLog { "Mikan, 同步错误: $id, error -> $it" }
        }
    }
}