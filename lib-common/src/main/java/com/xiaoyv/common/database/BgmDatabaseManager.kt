package com.xiaoyv.common.database

import androidx.room.Room
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.ResourceUtils
import com.blankj.utilcode.util.ZipUtils
import com.xiaoyv.blueprint.kts.launchProcess
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
            PathUtils.getInternalAppFilesPath() + "/database/subject.db"
        }

        private val remoteDbZipPath by lazy {
            PathUtils.getInternalAppFilesPath() + "/database/subject.db.zip"
        }

        private val remoteDbDir by lazy {
            PathUtils.getInternalAppFilesPath() + "/database"
        }

        private val manager by lazy {
            BgmDatabaseManager()
        }

        /**
         * 安装数据库
         */
        fun installAssetDb() {
            launchProcess {
                withContext(Dispatchers.IO) {
                    if (FileUtils.isFileExists(remoteDbPath) && FileUtils.getLength(remoteDbPath) > 0) return@withContext
                    ResourceUtils.copyFileFromAssets(
                        "config/subject/subject.db.zip",
                        remoteDbZipPath
                    )
                    ZipUtils.unzipFile(remoteDbZipPath, remoteDbDir)
                }

                debugLog { "数据库安装完成！" }
            }
        }

        val collection get() = manager.db.userDao()

        val subject get() = manager.remoteDb.subjectDao()
    }
}