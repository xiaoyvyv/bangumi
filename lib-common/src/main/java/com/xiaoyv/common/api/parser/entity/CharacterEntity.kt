package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.helper.IdEntity
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
    override var id: String = "",
    var nameCn: String = "",
    var nameNative: String = "",
    var avatar: String = "",
    var from: List<MediaDetailEntity.MediaRelative> = emptyList(),
    var comments: List<CommentTreeEntity> = emptyList()
) : Parcelable, IdEntity