package com.xiaoyv.common.database.collection

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.blankj.utilcode.util.TimeUtils
import com.xiaoyv.common.database.BgmDatabase
import com.xiaoyv.common.helper.callback.DiffEntity
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@Entity(tableName = BgmDatabase.TABLE_NAME_COLLECT)
data class Collection(
    @PrimaryKey(autoGenerate = true) override var id: Long = 0,
    @ColumnInfo(name = "uid") val uid: String,
    @ColumnInfo(name = "type") val type: Int,
    @ColumnInfo(name = "title") val title: String = "",
    @ColumnInfo(name = "content") val content: String = "",
    @ColumnInfo(name = "time") val time: Long = TimeUtils.getNowMills(),

    @ColumnInfo(name = "t_id") val tid: String,
    @ColumnInfo(name = "t_uid") val tUid: String = "",
    @ColumnInfo(name = "t_name") val tName: String = "",
    @ColumnInfo(name = "t_avatar") val tAvatar: String = "",
    @ColumnInfo(name = "t_image") val tImage: String = "",
    @ColumnInfo(name = "t_url") val tUrl: String = ""
) : Parcelable, DiffEntity<Long>
