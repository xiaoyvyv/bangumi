package com.xiaoyv.bangumi.special.thunder.torrent

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.config.bean.TorrentInfoWrap
import com.xiaoyv.widget.kts.orEmpty
import com.xunlei.downloadlib.parameter.TorrentInfo
import java.util.LinkedList

/**
 * Class: [TorrentInfoViewModel]
 *
 * @author why
 * @since 3/24/24
 */
class TorrentInfoViewModel : BaseListViewModel<TorrentInfoWrap>() {
    internal var torrentInfo: TorrentInfo? = null

    internal var nodeNavHistory = MutableLiveData<LinkedList<TorrentInfoWrap?>?>(LinkedList())
    private var rootNode: TorrentInfoWrap? = null

    fun setTorrentMap(parcelObj: TorrentInfo?) {
        torrentInfo = parcelObj
        nodeNavHistory.value?.clear()
    }

    override suspend fun onRequestListImpl(): List<TorrentInfoWrap> {
        if (rootNode == null) {
            rootNode = TorrentInfoWrap.convertNode(torrentInfo?.mSubFileInfo)
            nodeNavHistory.value?.clear()
            nodeNavHistory.value?.add(rootNode)
        }

        // 文件夹在上，其次按名称排序
        return nodeNavHistory.value?.lastOrNull()?.children.orEmpty()
            .sortedBy { it.dirPath.ifBlank { it.data?.mFileName.orEmpty() } }
            .sortedBy { if (it.dirPath.isNotBlank()) 0 else 1 }
    }

    /**
     * 是否还可以回退层级
     */
    fun canGoBack(): Boolean {
        return (nodeNavHistory.value?.size.orEmpty()) > 1
    }

    /**
     * 回退一级
     */
    fun goBack() {
        nodeNavHistory.value = nodeNavHistory.value?.also { it.removeLastOrNull() }
        refresh()
    }

    /**
     * 进入 Dir
     */
    fun gotoDir(dirNode: TorrentInfoWrap) {
        nodeNavHistory.value = nodeNavHistory.value?.also { it.add(dirNode) }
        refresh()
    }
}