package com.xiaoyv.common.database

import androidx.room.Room
import com.xiaoyv.common.currentApplication

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

    companion object {
        private val manager by lazy {
            BgmDatabaseManager()
        }

        val collection get() = manager.db.userDao()
    }
}