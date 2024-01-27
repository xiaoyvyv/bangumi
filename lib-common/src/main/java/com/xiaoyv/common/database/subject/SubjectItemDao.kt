package com.xiaoyv.common.database.subject

import androidx.room.Dao
import androidx.room.Query
import com.xiaoyv.common.database.BgmDatabaseRemote

@Dao
interface SubjectItemDao {

    @Query("SELECT * FROM ${BgmDatabaseRemote.TABLE_NAME_SUBJECT}")
    fun getAll(): List<SubjectItem>

    @Query("SELECT * FROM ${BgmDatabaseRemote.TABLE_NAME_SUBJECT} WHERE name = :name or name_cn = :name")
    fun getByName(name: String): List<SubjectItem>
}
