package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

/**
 * Class: [CharacterEntity]
 *
 * @author why
 * @since 12/6/23
 */
@Keep
@Parcelize
data class CharacterEntity(
    var id: String = "",
    var nameCn: String = "",
    var nameNative: String = "",
    var avatar: String = "",
    var from: List<MediaDetailEntity.MediaRelative> = emptyList()
) : Parcelable