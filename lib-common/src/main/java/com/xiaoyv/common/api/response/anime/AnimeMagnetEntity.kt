package com.xiaoyv.common.api.response.anime

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.xiaoyv.common.helper.callback.IdEntity
import com.xiaoyv.common.kts.debugLog
import kotlinx.parcelize.Parcelize


/**
 * Class: [AnimeMagnetEntity]
 *
 * @author why
 * @since 1/1/24
 */
@Keep
@Parcelize
data class AnimeMagnetEntity(
    @SerializedName("HasMore")
    var hasMore: Boolean = false,
    @SerializedName("Resources")
    var resources: List<Resource>? = null,
) : Parcelable {

    @Keep
    @Parcelize
    data class Resource(
        @SerializedName("FileSize")
        var fileSize: String? = null,
        @SerializedName("Magnet")
        var magnet: String? = null,
        @SerializedName("PageUrl")
        var pageUrl: String? = null,
        @SerializedName("PublishDate")
        var publishDate: String? = null,
        @SerializedName("SubgroupId")
        var subgroupId: Int = 0,
        @SerializedName("SubgroupName")
        var subgroupName: String? = null,
        @SerializedName("Title")
        var title: String? = null,
        @SerializedName("TypeId")
        var typeId: Int = 0,
        @SerializedName("TypeName")
        var typeName: String? = null,
    ) : Parcelable, IdEntity {
        override var id: String
            get() = pageUrl.orEmpty() + title.orEmpty()
            set(value) = debugLog { value }
    }
}
