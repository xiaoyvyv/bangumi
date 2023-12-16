package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
 * Class: [CreatePostEntity]
 *
 * @author why
 * @since 12/2/23
 */
@Keep
@Parcelize
data class CreatePostEntity(
    var title: String = "",
    var content: String = "",
    var tags: String = "",
    var isPublic: Boolean = true,
    var draft: Boolean = false,
) : Parcelable