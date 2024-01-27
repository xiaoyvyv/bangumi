package com.xiaoyv.script

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.xiaoyv.script.database.BgmScriptDataBase
import com.xiaoyv.script.database.dao.SubjectDao
import java.io.File
import java.util.concurrent.atomic.AtomicInteger


/**
 * Class: [Main]
 *
 * @author why
 * @since 1/15/24
 */
object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        parseScript()
    }

    private fun parseScript() {
        val json = File("/Users/why/Downloads/dump-2024-01-16.210308Z/subject.jsonlines")
        val gson = Gson()
        val i = AtomicInteger()
        json.forEachLine {
            val data = gson.fromJson(it, SubjectData::class.java)
            i.incrementAndGet()
            BgmScriptDataBase.instance.saveSubject(
                SubjectDao(
                    id = data.id,
                    name = data.name.orEmpty(),
                    name_cn = data.nameCn.orEmpty(),
                    nsfw = data.nsfw,
                    type = data.type
                )
            )
        }
        println(i.get())
    }

    data class SubjectData(
        @SerializedName("id")
        var id: Long = 0,
        @SerializedName("infobox")
        var infobox: String? = null,
        @SerializedName("name")
        var name: String? = null,
        @SerializedName("name_cn")
        var nameCn: String? = null,
        @SerializedName("nsfw")
        var nsfw: Boolean = false,
        @SerializedName("platform")
        var platform: Int = 0,
        @SerializedName("rank")
        var rank: Int = 0,
        @SerializedName("score")
        var score: Double = 0.0,
        @SerializedName("score_details")
        var scoreDetails: ScoreDetails? = null,
        @SerializedName("summary")
        var summary: String? = null,
        @SerializedName("tags")
        var tags: List<Tag>? = null,
        @SerializedName("type")
        var type: Int = 0,
    ) {
        data class ScoreDetails(
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
            var x9: Int = 0,
        )

        data class Tag(
            @SerializedName("count")
            var count: Int = 0,
            @SerializedName("name")
            var name: String? = null,
        )
    }
}