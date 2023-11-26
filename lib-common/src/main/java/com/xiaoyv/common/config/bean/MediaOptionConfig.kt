package com.xiaoyv.common.config.bean

import android.os.Parcelable
import androidx.annotation.Keep
import com.blankj.utilcode.util.ResourceUtils
import com.google.gson.annotations.SerializedName
import com.xiaoyv.common.config.annotation.BrowserSortType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.kts.currentYear
import com.xiaoyv.common.kts.fromJson
import kotlinx.parcelize.Parcelize


/**
 * Class: [MediaOptionConfig]
 *
 * @author why
 * @since 11/26/23
 */
@Keep
@Parcelize
class MediaOptionConfig : ArrayList<MediaOptionConfig.Config>(), Parcelable {

    @Keep
    @Parcelize
    data class Config(
        @SerializedName("mediaType")
        var mediaType: String? = null,
        @SerializedName("option")
        var option: List<Option>? = null
    ) : Parcelable {

        @Keep
        @Parcelize
        data class Option(
            @SerializedName("items")
            var items: List<Item>? = null,
            @SerializedName("pathIndex")
            var pathIndex: Int = 0,
            @SerializedName("title")
            var title: String? = null,
            @SerializedName("generate")
            var generate: Boolean = false,
        ) : Parcelable {

            @Keep
            @Parcelize
            data class Item(
                @SerializedName("groupTitle")
                var groupTitle: String? = null,
                @SerializedName("pathIndex")
                var pathIndex: Int = 0,
                @SerializedName("mediaType")
                var mediaType: String? = null,
                @SerializedName("title")
                var title: String? = null,
                @SerializedName("value")
                var value: String? = null,
                @SerializedName("isYear")
                var isYear: Boolean = false,

                /**
                 * 下面的互斥
                 */
                @SerializedName("isPinYin")
                var isSortPinYin: Boolean = false,
                @SerializedName("isSortOption")
                var isSortOption: Boolean = false,
            ) : Parcelable
        }
    }

    companion object {
        private val config: MediaOptionConfig by lazy {
            ResourceUtils.readAssets2String("config/media.options.json")
                .fromJson<MediaOptionConfig>() ?: MediaOptionConfig()
        }

        /**
         * 创建媒体查询参数
         */
        fun buildMediaOptions(fromYear: Int = currentYear + 1): MediaOptionConfig {
            // 补充时间拼音等选项
            config.onEach {
                val mediaType = it.mediaType.orEmpty()
                val option = it.option.orEmpty().toMutableList()
                option.removeIf { item -> item.generate }
                option.add(buildTimeOption(mediaType, fromYear))
                option.add(buildSort(mediaType))
                option.add(buildPinYinOption(mediaType))
                it.option = option
            }

            return config
        }

        private fun buildSort(@MediaType mediaType: String): Config.Option {
            val option = Config.Option()
            option.title = "排序类型"
            option.pathIndex = -1
            option.generate = true
            option.items = listOf(
                "排名" to BrowserSortType.TYPE_RANK,
                "名称" to BrowserSortType.TYPE_TITLE,
                "日期" to BrowserSortType.TYPE_DATE
            ).map {
                Config.Option.Item(
                    groupTitle = option.title.orEmpty(),
                    pathIndex = option.pathIndex,
                    mediaType = mediaType,
                    title = it.first,
                    value = it.second,
                    isSortOption = true,
                )
            }
            return option
        }


        private fun buildPinYinOption(@MediaType mediaType: String): Config.Option {
            val option = Config.Option()
            option.title = "排序拼音"
            option.pathIndex = -1
            option.generate = true
            option.items = ('A'..'Z').toList().toCharArray().map {
                Config.Option.Item(
                    groupTitle = option.title.orEmpty(),
                    pathIndex = option.pathIndex,
                    mediaType = mediaType,
                    title = it.toString(),
                    value = it.toString(),
                    isSortPinYin = true,
                )
            }
            return option
        }

        private fun buildTimeOption(mediaType: String, fromYear: Int): Config.Option {
            val option = Config.Option()
            option.title = "时间"
            option.pathIndex = 10
            option.generate = true
            option.items = arrayListOf<Config.Option.Item>().apply {
                var tmp = fromYear
                repeat(20) {
                    add(
                        Config.Option.Item(
                            groupTitle = option.title.orEmpty(),
                            pathIndex = option.pathIndex,
                            mediaType = mediaType,
                            title = tmp.toString() + "年",
                            value = tmp.toString(),
                            isYear = true,
                        )
                    )
                    tmp--
                }
            }
            return option
        }
    }
}