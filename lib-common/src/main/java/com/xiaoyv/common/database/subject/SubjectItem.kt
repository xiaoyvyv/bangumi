package com.xiaoyv.common.database.subject

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.xiaoyv.common.config.annotation.SubjectType
import com.xiaoyv.common.database.BgmDatabaseRemote
import com.xiaoyv.common.helper.callback.DiffEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [SubjectItem]
 *
 * @author why
 * @since 1/26/24
 */
@Keep
@Parcelize
@Entity(
    tableName = BgmDatabaseRemote.TABLE_NAME_SUBJECT,
    indices = [
        Index(value = ["name"], name = "bgm_subject_name_index"),
        Index(value = ["name_cn"], name = "bgm_subject_name_cn_index"),
    ]
)
data class SubjectItem(
    @PrimaryKey(autoGenerate = true) override var id: Long? = 0,
    @ColumnInfo(name = "name") var name: String? = null,
    @ColumnInfo(name = "name_cn") var nameCn: String? = null,
    @ColumnInfo(name = "type") @SubjectType var type: Int? = null,
    @ColumnInfo(name = "nsfw") var nsfw: Boolean? = false,
) : Parcelable, DiffEntity<Long?>
