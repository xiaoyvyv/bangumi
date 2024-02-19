package com.xiaoyv.common.database.collection

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.xiaoyv.common.config.annotation.LocalCollectionType
import com.xiaoyv.common.database.BgmDatabase

@Dao
interface CollectionDao {
    @Query("SELECT * FROM ${BgmDatabase.TABLE_NAME_COLLECT}")
    fun getAll(): List<Collection>

    @Query("SELECT * FROM ${BgmDatabase.TABLE_NAME_COLLECT} WHERE type = :type")
    fun loadAllByType(@LocalCollectionType type: Int): List<Collection>

    @Query("SELECT COUNT(*) FROM ${BgmDatabase.TABLE_NAME_COLLECT} WHERE t_id = :tid AND type = :type")
    fun isCollected(tid: String, type: Int): Boolean

    @Insert
    fun insertAll(vararg users: Collection)

    @Query("DELETE FROM ${BgmDatabase.TABLE_NAME_COLLECT} WHERE t_id = :tid AND type = :type")
    fun delete(tid: String, @LocalCollectionType type: Int)

    @Query("DELETE FROM ${BgmDatabase.TABLE_NAME_COLLECT}")
    fun deleteAll()
}
