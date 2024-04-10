package com.xiaoyv.common.helper

import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ResourceUtils
import com.xiaoyv.blueprint.kts.launchProcess
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.fromJson
import com.xiaoyv.widget.kts.workDirOf
import kotlinx.coroutines.Dispatchers

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

            // 更新远程数据
            runCatching {
                idMap.putAll(BgmApiManager.bgmWebApi.queryMikanIdMapByJsdelivr())
            }.onFailure {
                debugLog { "Mikan, js-cdn sync error: " + it.printStackTrace() }

                // cdn 失败用 github
                runCatching {
                    idMap.putAll(BgmApiManager.bgmWebApi.queryMikanIdMapByGithub())
                }.onFailure {
                    debugLog { "Mikan, github sync error: " + it.printStackTrace() }
                }
            }

            debugLog { "Mikan, Maps: " + idMap.size }

            FileIOUtils.writeFileFromString(mikanMapFile, idMap.toJson())
        }
    }

    /**
     * 根据 bgmId 获取 mikanId
     */
    fun getMikanMapId(bgmId: String?): String {
        bgmId ?: return ""
        val entry = idMap.entries.find { it.value == bgmId }
        return entry?.key.orEmpty()
    }
}