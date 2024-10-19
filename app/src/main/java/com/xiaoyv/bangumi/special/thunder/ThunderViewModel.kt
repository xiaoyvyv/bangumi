package com.xiaoyv.bangumi.special.thunder

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.UriUtils
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.i18n
import com.xiaoyv.thunder.Thunder
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.showToastCompat
import com.xiaoyv.widget.kts.workDirOf
import com.xunlei.downloadlib.parameter.TorrentInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [ThunderViewModel]
 *
 * @author why
 * @since 3/23/24
 */
class ThunderViewModel : BaseViewModel() {
    internal val onTorrentUriLiveData = MutableLiveData<Uri?>()
    internal val onIntentUriLiveData = MutableLiveData<Uri?>()

    /**
     * 解析的种子信息
     */
    internal val onTorrentInfoLiveData = MutableLiveData<TorrentInfo?>()

    internal val supportScheme = listOf("magnet", "thunder", "ftp", "ed2k", "http", "https")

    /**
     * BT 文件保存路径
     */
    private val btSaveDir by lazy {
        workDirOf("download/files/torrent")
    }

    /**
     * 处理链接
     */
    fun handleLinkUri(uri: Uri) {
        debugLog { "Uri: $uri" }

        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()

                showToastCompat(it.errorMsg)
            },
            block = {
                withContext(Dispatchers.IO) {
                    require(supportScheme.contains(uri.scheme)) {
                        i18n(CommonString.download_support_scheme, supportScheme)
                    }

                    // 如果是磁力链接则先获取种子文件
                    val url = Thunder.instance.convertUrl(uri.toString())
                    if (url.startsWith("magnet")) {
                        val torrentPath = Thunder.instance.downloadMagnetTorrent(url, btSaveDir)
                        requireNotNull(torrentPath) { i18n(CommonString.download_magnet_error) }
                        handleTorrentUri(UriUtils.file2Uri(FileUtils.getFileByPath(torrentPath)))
                    }
                    // 普通链接
                    else {
                        Thunder.instance.crateNormalTask(url)
                    }
                }
            }
        )
    }

    /**
     * 处理种子文件
     */
    fun handleTorrentUri(uri: Uri) {
        debugLog { "Uri: $uri" }

        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()

                showToastCompat(it.errorMsg)
            },
            block = {
                onTorrentInfoLiveData.value = withContext(Dispatchers.IO) {
                    Thunder.instance.queryTorrentInfo(UriUtils.uri2File(uri).absolutePath)
                }
            }
        )
    }
}