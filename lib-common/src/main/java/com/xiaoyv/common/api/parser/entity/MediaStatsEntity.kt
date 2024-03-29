package com.xiaoyv.common.api.parser.entity

import android.graphics.Color
import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize


/**
 * Class: [MediaStatsEntity]
 *
 * @author why
 * @since 1/11/24
 */
@Keep
@Parcelize
data class MediaStatsEntity(
    @SerializedName("interest_type")
    var interestType: TypeData? = null,
    @SerializedName("regdate")
    var regDate: TypeData? = null,
    @SerializedName("total_collects")
    var totalCollects: TypeData? = null,
    @SerializedName("vib")
    var vib: TypeData? = null,
    var interestGridState: List<GridState>? = null,
    var vibGridState: List<GridState>? = null,
) : Parcelable {

    @Keep
    @Parcelize
    data class GridState(
        override var id: String = "",
        var title: String = "",
        var desc: String = "",
        var color: Int = Color.GRAY,
    ) : Parcelable, IdEntity

    @Keep
    @Parcelize
    data class TypeData(
        @SerializedName("chart_root")
        var chartRoot: String? = null,
        @SerializedName("data")
        var dataMap: List<Map<String, Int>>? = null,
        @SerializedName("desc")
        var desc: String? = null,
        @SerializedName("enable_score")
        var enableScore: Boolean = false,
        @SerializedName("series_set")
        var seriesSet: Map<String, String>? = null,
        @SerializedName("title")
        var title: String? = null,
    ) : Parcelable
}