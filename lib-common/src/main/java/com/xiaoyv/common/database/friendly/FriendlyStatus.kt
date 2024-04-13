package com.xiaoyv.common.database.friendly

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import com.xiaoyv.common.database.BgmDatabase
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class FriendlyStatus(
    @ColumnInfo("uid") var uid: String,
    @ColumnInfo("score_count") var scoreCount: Int
) : Parcelable
