package com.xiaoyv.common.api.response

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * Class: [CalendarEntity]
 *
 * @author why
 * @since 11/25/23
 */
@Keep
@Parcelize
class CalendarEntity : ArrayList<CalendarEntity.CalendarEntityItem>(), Parcelable {
    @Keep
    @Parcelize
    data class CalendarEntityItem(
        @SerializedName("items")
        var items: List<Item>? = null,
        @SerializedName("weekday")
        var weekday: Weekday? = null
    ) : Parcelable {
        @Keep
        @Parcelize
        data class Item(
            @SerializedName("air_date")
            var airDate: String? = null,
            @SerializedName("air_weekday")
            var airWeekday: Int = 0,
            @SerializedName("collection")
            var collection: Collection? = null,
            @SerializedName("eps")
            var eps: Int = 0,
            @SerializedName("eps_count")
            var epsCount: Int = 0,
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("images")
            var images: Images? = null,
            @SerializedName("name")
            var name: String? = null,
            @SerializedName("name_cn")
            var nameCn: String? = null,
            @SerializedName("rank")
            var rank: Int = 0,
            @SerializedName("rating")
            var rating: Rating? = null,
            @SerializedName("summary")
            var summary: String? = null,
            @SerializedName("type")
            var type: Int = 0,
            @SerializedName("url")
            var url: String? = null
        ) : Parcelable {
            @Keep
            @Parcelize
            data class Collection(
                @SerializedName("collect")
                var collect: Int = 0,
                @SerializedName("doing")
                var doing: Int = 0,
                @SerializedName("dropped")
                var dropped: Int = 0,
                @SerializedName("on_hold")
                var onHold: Int = 0,
                @SerializedName("wish")
                var wish: Int = 0
            ) : Parcelable

            @Keep
            @Parcelize
            data class Images(
                @SerializedName("common")
                var common: String? = null,
                @SerializedName("grid")
                var grid: String? = null,
                @SerializedName("large")
                var large: String? = null,
                @SerializedName("medium")
                var medium: String? = null,
                @SerializedName("small")
                var small: String? = null
            ) : Parcelable

            @Keep
            @Parcelize
            data class Rating(
                @SerializedName("count")
                var count: Count? = null,
                @SerializedName("score")
                var score: Double = 0.0,
                @SerializedName("total")
                var total: Int = 0
            ) : Parcelable {
                @Keep
                @Parcelize
                data class Count(
                    @SerializedName("1")
                    var x1: Int = 0,
                    @SerializedName("10")
                    var x10: Int = 0,
                    @SerializedName("2")
                    var x2: Int = 0,
                    @SerializedName("3")
                    var x3: Int = 0,
                    @SerializedName("4")
                    var x4: Int = 0,
                    @SerializedName("5")
                    var x5: Int = 0,
                    @SerializedName("6")
                    var x6: Int = 0,
                    @SerializedName("7")
                    var x7: Int = 0,
                    @SerializedName("8")
                    var x8: Int = 0,
                    @SerializedName("9")
                    var x9: Int = 0
                ) : Parcelable
            }
        }

        @Keep
        @Parcelize
        data class Weekday(
            @SerializedName("cn")
            var cn: String? = null,
            @SerializedName("en")
            var en: String? = null,
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("ja")
            var ja: String? = null
        ) : Parcelable
    }
}