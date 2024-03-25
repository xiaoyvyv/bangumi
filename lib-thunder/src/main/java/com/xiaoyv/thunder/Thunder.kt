package com.xiaoyv.thunder

import android.app.Application
import android.os.Build
import com.xunlei.downloadlib.XLDownloadManager
import com.xunlei.downloadlib.android.XLLog
import com.xunlei.downloadlib.parameter.GetFileName
import com.xunlei.downloadlib.parameter.GetTaskId
import com.xunlei.downloadlib.parameter.InitParam
import com.xunlei.downloadlib.parameter.MagnetTaskParam
import com.xunlei.downloadlib.parameter.TorrentInfo
import com.xunlei.downloadlib.parameter.XLConstant
import com.xunlei.downloadlib.parameter.XLTaskInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Class: [Thunder]
 *
 * @author why
 * @since 3/23/24
 */
class Thunder private constructor() {
    private val manager get() = XLDownloadManager.instance

    /**
     * 初始化下载引擎
     */
    fun init(context: Application, initParam: InitParam) {
        manager.init(context, initParam)
        manager.setOSVersion(Build.VERSION.INCREMENTAL)
        manager.setSpeedLimit(-1, -1)
        manager.setVipType("1")
        manager.setUserId("2000017962")
        manager.setUploadSwitch(false)
    }

    /**
     * 获取全部下载任务
     *
     * @param isFinish 是否是已完成的任务
     */
    fun queryTaskList(isFinish: Boolean): List<XLTaskInfo> {
        return listOf()
    }

    /**
     * 转换链接，如 thunder://xxx
     */
    fun convertUrl(url: String): String {
        return manager.parserThunderUrl(url).ifBlank { url }
    }

    /**
     * 正常链接下载 "ftp", "ed2k", "http", "https"
     */
    fun crateNormalTask(url: String) {

    }

    /**
     * 磁力链接下载 "magnet" 的种子文件
     */
    suspend fun downloadMagnetTorrent(url: String, saveDir: String): String? {
        // 获取磁力链接的种子名字
        val btFileName = GetFileName().let {
            manager.getFileNameFromUrl(url, it)
            it.mFileName.orEmpty().ifBlank { System.currentTimeMillis().toString() + ".torrent" }
        }
        val btFilePath = saveDir + File.separator + btFileName

        XLLog.i(TAG, "Magnet fileName = $btFileName")

        val taskId = GetTaskId().let {
            manager.createBtMagnetTask(
                magnetTaskParam = MagnetTaskParam(
                    mFileName = btFileName,
                    mFilePath = saveDir,
                    mUrl = url
                ),
                getTaskId = it
            )
            it.mTaskId
        }

        XLLog.i(TAG, "Magnet taskId = $taskId")

        manager.startTask(taskId)
        manager.setTaskSpeedLimit(taskId, -1)

        val xlTaskInfo = repeatCheck {
            val info = XLTaskInfo()
            manager.getTaskInfo(taskId, 0, info)

            when (info.mTaskStatus) {
                XLConstant.XLTaskStatus.TASK_IDLE -> (false to info)
                XLConstant.XLTaskStatus.TASK_RUNNING -> (false to info)
                XLConstant.XLTaskStatus.TASK_SUCCESS -> (true to info)
                XLConstant.XLTaskStatus.TASK_FAILED -> (true to info)
                XLConstant.XLTaskStatus.TASK_STOPPED -> (true to info)
                else -> (false to info)
            }
        }

        if (xlTaskInfo == null) return null

        // 返回种子信息
        return btFilePath
    }


    /**
     * 重复执行逻辑，直到返回值满足条件 默认 30 次，间隔 1s
     *
     * @param action 每次迭代执行的逻辑，返回值 first 表示是否已完成，完成了则跳出，second 表示返回数据
     */
    private suspend fun <T> repeatCheck(
        times: Int = 30,
        mills: Long = 1000,
        action: suspend CoroutineScope.() -> Pair<Boolean, T?> = { false to null },
    ): T? {
        var obj: T? = null
        withContext(Dispatchers.IO) {
            var count = 0
            while (isActive && count < times) {
                val condition = action()
                if (condition.first) {
                    obj = condition.second
                    break
                }
                count++
                if (count < times) delay(mills)
            }
        }
        return obj
    }

    /**
     * 解析种子信息
     */
    fun queryTorrentInfo(torrentPath: String): TorrentInfo {
        return TorrentInfo().apply {
            manager.getTorrentInfo(torrentPath, this)
        }
    }


    companion object {
        private const val TAG = "Thunder"

        val instance by lazy { Thunder() }
    }
}