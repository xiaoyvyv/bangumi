package com.xiaoyv.script

import com.google.gson.Gson
import org.jsoup.Jsoup
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import kotlin.system.exitProcess

/**
 * Class: [MiKan]
 *
 * @author why
 * @since 3/20/24
 */
object MiKan {

    private val idMap = hashMapOf<String, String>()

    @JvmStatic
    fun main(args: Array<String>) {
        val ids = arrayListOf<Int>()
        val total = AtomicInteger(5000)
        val finish = AtomicInteger(0)
        val gson = Gson()
        val file =
            File("/Users/why/AndroidStudioProjects/Bangumi/lib-script/src/main/java/com/xiaoyv/script/mikan.json")

        repeat(total.get()) {
            ids.add(it + 3000)
        }

        ids.chunked(300).forEach { items ->
            thread {
                items.forEach { id ->
                    parserId(id)
                    val get = finish.incrementAndGet()
                    System.err.println("总共: ${total.get()}，已完成：${get}，剩余：${total.get() - get}，Map：${idMap.size}")

                }
            }
        }

        thread {
            while (true) {
                if (finish.get() == total.get()) {
                    val sortedMap = idMap.toSortedMap { p0, p1 ->
                        p0.toInt() - p1.toInt()
                    }
                    val json = gson.toJson(sortedMap)
                    file.writeText(json)
                    break
                }
            }
            exitProcess(0)
        }
    }

    private fun parserId(i: Int) {
        runCatching {
            val response = Jsoup.connect("https://mikanani.me/Home/Bangumi/$i")
                .ignoreContentType(true)
                .followRedirects(false)
                .timeout(10000)
                .execute()

            require(response.statusCode() in (200..299)) { "MiKan: $i, code: ${response.statusCode()}" }

            val document = response.parse()
            val links = document.select("a.w-other-c")
            val element = links.find { it.text().contains("bgm.tv/subject") }
            requireNotNull(element) { "MiKan: $i, Bgm id is empty!" }
            val bgmId = element.text().substringAfterLast("/").trim()
            idMap[i.toString()] = bgmId

            System.err.println("$i -> $bgmId")
        }.onFailure {
            System.err.println(it.message)
        }
    }
}