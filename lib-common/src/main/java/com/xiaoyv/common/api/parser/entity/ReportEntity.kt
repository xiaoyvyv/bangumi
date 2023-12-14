package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
 * Class: [ReportEntity]
 *
 * @author why
 * @since 12/14/23
 */
@Keep
@Parcelize
data class ReportEntity(
    var hiddenForm: Map<String, String> = emptyMap(),
) : Parcelable
