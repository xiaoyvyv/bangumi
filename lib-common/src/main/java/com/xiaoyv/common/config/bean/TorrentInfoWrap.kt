package com.xiaoyv.common.config.bean

import android.os.Parcelable
import androidx.annotation.Keep
import com.xunlei.downloadlib.parameter.TorrentFileInfo
import kotlinx.parcelize.Parcelize

/**
 * Class: [TorrentInfoWrap]
 *
 * @author why
 * @since 3/24/24
 */
@Keep
@Parcelize
data class TorrentInfoWrap(
    var dirPath: String = "",
    var data: TorrentFileInfo? = null,
    var children: MutableList<TorrentInfoWrap> = mutableListOf(),
) : Parcelable {

    val isDir: Boolean
        get() = dirPath.isNotBlank()

    companion object {

        /**
         * 将集合类型的 TorrentFileInfo 转为 TreeNode 结构
         */
        fun convertNode(list: Array<TorrentFileInfo>?): TorrentInfoWrap {
            val root = TorrentInfoWrap(dirPath = "/")
            val fileInfos = list ?: return root

            // 文件夹类型节点缓存
            val nodeMap = mutableMapOf<String, TorrentInfoWrap>()

            // 根节点
            nodeMap["/"] = root

            for (item in fileInfos) {
                val subPath = item.mSubPath.orEmpty()

                // 没有子路径情况下
                if (subPath.isBlank()) {
                    root.children.add(TorrentInfoWrap(data = item))
                } else {

                    // 有子路径
                    val subPaths = subPath.split("/")

                    var currentNode = root
                    var subPathTmp = "/"
                    for (sub in subPaths) {
                        subPathTmp = "$subPathTmp$sub/"

                        val subDirNode = nodeMap.getOrPut(subPathTmp) {
                            TorrentInfoWrap(
                                data = TorrentFileInfo(mFileName = sub),
                                dirPath = subPathTmp
                            )
                        }

                        if (currentNode.children.find { it.dirPath == subPathTmp } == null) {
                            currentNode.children.add(subDirNode)
                        }

                        currentNode = subDirNode
                    }

                    currentNode.children.add(TorrentInfoWrap(data = item))
                }
            }

            nodeMap.clear()

            return root
        }
    }
}
