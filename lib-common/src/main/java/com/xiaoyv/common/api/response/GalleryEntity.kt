package com.xiaoyv.common.api.response

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [GalleryEntity]
 *
 * @author why
 * @since 12/20/23
 */
@Keep
@Parcelize
data class GalleryEntity(
    override var id: String = "",
    var height: Int = 0,
    var width: Int = 0,
    var imageUrl: String = "",
    var largeImageUrl: String = "",
    var size: Long = 0,
) : IdEntity, Parcelable
