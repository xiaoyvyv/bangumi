package com.xiaoyv.common.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.xiaoyv.common.database.subject.SubjectItem
import com.xiaoyv.common.database.subject.SubjectItemDao

/**
 * Class: [BgmDatabaseRemote]
 *
 * @author why
 * @since 1/26/24
 */
@Database(entities = [SubjectItem::class], version = BgmDatabase.DATABASE_VERSION)
abstract class BgmDatabaseRemote : RoomDatabase() {

    abstract fun subjectDao(): SubjectItemDao

    companion object {
        const val TABLE_NAME_SUBJECT = "bgm_subject"
    }
}