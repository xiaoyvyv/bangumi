package com.xiaoyv.bangumi.special.collection

import android.net.Uri
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.UriUtils
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.config.annotation.CollectionType
import com.xiaoyv.common.database.BgmDatabaseManager
import com.xiaoyv.common.database.collection.Collection
import com.xiaoyv.common.helper.CollectionHelper
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.fromJson
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.showToastCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [CollectionViewModel]
 *
 * @author why
 * @since 1/3/24
 */
class CollectionViewModel : BaseListViewModel<Collection>() {
    internal val onExportLivaData = UnPeekLiveData<Uri?>()

    @CollectionType
    internal var type: Int = 0

    override suspend fun onRequestListImpl(): List<Collection> {
        return BgmDatabaseManager.collection.loadAllByType(type)
    }

    fun deleteCollection(collection: Collection) {
        launchUI {
            CollectionHelper.deleteCollect(collection.tid, collection.type)
            refresh()
        }
    }

    /**
     * 导入收藏数据
     */
    fun importData(uri: Uri) {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()
                showToastCompat(it.errorMsg)
            },
            block = {
                withContext(Dispatchers.IO) {
                    val collections = UriUtils.uri2Bytes(uri).decodeToString()
                        .fromJson<List<Collection>>()
                        .orEmpty()

                    require(collections.isNotEmpty()) {
                        "导入文件格式不支持，请使用该软件导出的文件数据"
                    }

                    // 对比插入本地数据库
                    CollectionHelper.mergeIntoLocal(collections, false)
                }

                showToastCompat("导入成功！")
            }
        )
    }

    /**
     * 导出收藏数据
     */
    fun exportData() {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()
                showToastCompat(it.errorMsg)
            },
            block = {
                onExportLivaData.value = withContext(Dispatchers.IO) {
                    val data = BgmDatabaseManager.collection.getAll().toJson(true)
                    val cacheFile =
                        PathUtils.getCachePathExternalFirst() + "/collection/collection_${UserHelper.currentUser.id}.json"
                    FileIOUtils.writeFileFromString(cacheFile, data)
                    UriUtils.file2Uri(FileUtils.getFileByPath(cacheFile))
                }
            }
        )
    }
}