package com.xiaoyv.common.database.friendly

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.xiaoyv.common.config.annotation.SubjectCollectType
import com.xiaoyv.common.config.annotation.SubjectType
import com.xiaoyv.common.database.BgmDatabase
import com.xiaoyv.common.helper.callback.DiffEntity
import kotlinx.parcelize.Parcelize


/**
 * 友评数据库
 */
@Keep
@Parcelize
@Entity(tableName = BgmDatabase.TABLE_NAME_FRIEND_RANK)
open class FriendlyRank(
    @PrimaryKey(autoGenerate = true) override var id: Long = 0,

    @ColumnInfo("master") open var master: String = "",
    @ColumnInfo("uid") open var uid: String = "",
    @ColumnInfo("comment") open var comment: String? = null,
    @ColumnInfo("rate") open var rate: Int = 0,
    @ColumnInfo("type") @SubjectCollectType open var type: Int = 0,
    @ColumnInfo("updated_at") open var updatedAt: String? = null,
    @ColumnInfo("vol_status") open var volStatus: Int = 0,
    @ColumnInfo("ep_status") open var epStatus: Int = 0,

    @ColumnInfo("subject_id") open var subjectId: Long = 0,
    @ColumnInfo("subject_type") @SubjectType open var subjectType: Int = 0,
    @ColumnInfo("subject_date") open var subjectDate: String? = null,
    @ColumnInfo("subject_eps") open var subjectEps: Int = 0,
    @ColumnInfo("subject_volumes") open var subjectVolumes: Int = 0,
    @ColumnInfo("subject_score") open var subjectScore: Double = 0.0,
    @ColumnInfo("subject_cover") open var subjectCover: String? = null,
    @ColumnInfo("subject_name") open var subjectName: String? = null,
    @ColumnInfo("subject_name_cn") open var subjectNameCn: String? = null,
    @ColumnInfo("subject_short_summary") open var subjectShortSummary: String? = null
) : Parcelable, DiffEntity<Long>

@Keep
@Parcelize
class FriendlyRankEntity(
    @PrimaryKey(autoGenerate = true) override var id: Long = 0,

    @ColumnInfo("master") override var master: String = "",
    @ColumnInfo("uid") override var uid: String = "",
    @ColumnInfo("comment") override var comment: String? = null,
    @ColumnInfo("rate") override var rate: Int = 0,
    @ColumnInfo("type") @SubjectCollectType override var type: Int = 0,
    @ColumnInfo("updated_at") override var updatedAt: String? = null,
    @ColumnInfo("vol_status") override var volStatus: Int = 0,
    @ColumnInfo("ep_status") override var epStatus: Int = 0,

    @ColumnInfo("subject_id") override var subjectId: Long = 0,
    @ColumnInfo("subject_type") @SubjectType override var subjectType: Int = 0,
    @ColumnInfo("subject_date") override var subjectDate: String? = null,
    @ColumnInfo("subject_eps") override var subjectEps: Int = 0,
    @ColumnInfo("subject_volumes") override var subjectVolumes: Int = 0,
    @ColumnInfo("subject_score") override var subjectScore: Double = 0.0,
    @ColumnInfo("subject_cover") override var subjectCover: String? = null,
    @ColumnInfo("subject_name") override var subjectName: String? = null,
    @ColumnInfo("subject_name_cn") override var subjectNameCn: String? = null,
    @ColumnInfo("subject_short_summary") override var subjectShortSummary: String? = null,
    @ColumnInfo("friendly_rate") var friendlyRate: Double = 0.0,
    @ColumnInfo("friendly_count") var friendlyCount: Int = 0
) : FriendlyRank(
    id,
    master,
    uid,
    comment,
    rate,
    type,
    updatedAt,
    volStatus,
    epStatus,
    subjectId,
    subjectType,
    subjectDate,
    subjectEps,
    subjectVolumes,
    subjectScore,
    subjectCover,
    subjectName,
    subjectNameCn,
    subjectShortSummary
)
