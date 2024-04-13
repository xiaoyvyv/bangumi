package com.xiaoyv.common.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.xiaoyv.common.database.collection.Collection
import com.xiaoyv.common.database.collection.CollectionDao
import com.xiaoyv.common.database.friendly.FriendlyRank
import com.xiaoyv.common.database.friendly.FriendlyRankDao

@Database(
    entities = [Collection::class, FriendlyRank::class],
    version = BgmDatabase.DATABASE_VERSION
)
abstract class BgmDatabase : RoomDatabase() {

    abstract fun userDao(): CollectionDao

    abstract fun friendlyRankDao(): FriendlyRankDao

    companion object {
        const val DATABASE_VERSION = 6

        const val DATABASE_NAME = "bgm"
        const val TABLE_NAME_COLLECT = "collect"
        const val TABLE_NAME_FRIEND_RANK = "friendly_rank"
    }
}
