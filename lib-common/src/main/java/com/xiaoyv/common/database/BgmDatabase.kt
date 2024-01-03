package com.xiaoyv.common.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.xiaoyv.common.database.collection.Collection
import com.xiaoyv.common.database.collection.CollectionDao

@Database(entities = [Collection::class], version = BgmDatabase.DATABASE_VERSION)
abstract class BgmDatabase : RoomDatabase() {

    abstract fun userDao(): CollectionDao

    companion object {
        const val DATABASE_VERSION = 2

        const val DATABASE_NAME = "bgm"
        const val TABLE_NAME_COLLECT = "collect"
    }
}
