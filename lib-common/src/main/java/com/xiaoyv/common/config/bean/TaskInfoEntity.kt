package com.xiaoyv.common.config.bean

import android.os.Parcelable
import com.xiaoyv.common.helper.callback.IdEntity
import com.xunlei.downloadlib.parameter.XLTaskInfo
import kotlinx.parcelize.Parcelize

/**
 * Class: [TaskInfoEntity]
 *
 * @author why
 * @since 3/23/24
 */
@Parcelize
data class TaskInfoEntity(
    override var id: String = "",
    var xlTaskInfo: XLTaskInfo? = null,
) : Parcelable, IdEntity
