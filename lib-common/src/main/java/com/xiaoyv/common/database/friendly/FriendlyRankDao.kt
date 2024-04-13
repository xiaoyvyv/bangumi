package com.xiaoyv.common.database.friendly

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.xiaoyv.common.database.BgmDatabase

@Dao
interface FriendlyRankDao {

    /**
     * 友评榜
     *
     * @param scoreCount 每个条目最低几个人评分参与排名，默认超过一个评分就参与排名
     * @param offset 偏移
     * @param limit 数目
     */
    @Query("SELECT *, AVG(rate) AS friendly_rate, COUNT(*) AS friendly_count FROM ${BgmDatabase.TABLE_NAME_FRIEND_RANK} WHERE rate != 0  GROUP BY subject_id HAVING COUNT(*) >= :scoreCount ORDER BY friendly_rate DESC, subject_score DESC, friendly_count DESC LIMIT :offset, :limit")
    fun getFriendRank(scoreCount: Int = 1, offset: Int, limit: Int): List<FriendlyRankEntity>

    @Query("SELECT uid,COUNT(uid) AS score_count FROM friendly_rank GROUP BY uid")
    fun getStatus(): List<FriendlyStatus>

    @Insert
    fun insertAll(vararg users: FriendlyRank)

    @Insert
    fun insertAll(users: List<FriendlyRank>)

    @Query("DELETE FROM ${BgmDatabase.TABLE_NAME_FRIEND_RANK}")
    fun deleteAll()
}
