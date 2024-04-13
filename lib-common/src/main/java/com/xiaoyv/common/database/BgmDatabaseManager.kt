package com.xiaoyv.common.database

import android.net.Uri
import androidx.room.Room
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.ZipUtils
import com.xiaoyv.common.currentApplication
import com.xiaoyv.common.kts.debugLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [BgmDatabaseManager]
 *
 * @author why
 * @since 1/3/24
 */
class BgmDatabaseManager private constructor() {

    private val db by lazy {
        Room.databaseBuilder(currentApplication, BgmDatabase::class.java, BgmDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    private val remoteDb by lazy {
        Room.databaseBuilder(
            currentApplication,
            BgmDatabaseRemote::class.java,
            remoteDbPath
        ).fallbackToDestructiveMigration()
            .build()
    }

    companion object {
        private val remoteDbPath by lazy {
            PathUtils.getFilesPathExternalFirst() + "/database/subject.db"
        }

        private val remoteDbZipPath by lazy {
            PathUtils.getInternalAppFilesPath() + "/database/subject.db.zip"
        }

        private val remoteDbDir by lazy {
            PathUtils.getFilesPathExternalFirst() + "/database"
        }

        private val manager by lazy {
            BgmDatabaseManager()
        }

        /**
         * 题目数据库是否安装
         */
        fun isSubjectInstalled(): Boolean {
            return FileUtils.isFileExists(remoteDbPath)
        }

        /**
         * 安装数据库
         */
        suspend fun installAssetDb(uri: Uri) {
            withContext(Dispatchers.IO) {
                FileUtils.createOrExistsDir(remoteDbDir)
                FileUtils.deleteAllInDir(remoteDbDir)

                currentApplication.contentResolver.openInputStream(uri).use {
                    FileIOUtils.writeFileFromIS(remoteDbZipPath, it)
                }
                ZipUtils.unzipFile(remoteDbZipPath, remoteDbDir)

                if (!isSubjectInstalled()) {
                    FileUtils.deleteAllInDir(remoteDbDir)
                    throw IllegalArgumentException("数据库文件不合法")
                }

                debugLog { "数据库安装完成！" }
            }
        }

        val collection get() = manager.db.userDao()

        val friendlyRank get() = manager.db.friendlyRankDao()

        val subject get() = manager.remoteDb.subjectDao()
    }
}