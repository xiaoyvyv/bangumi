package com.xiaoyv.script

import com.xiaoyv.script.database.BgmScriptDataBase
import com.xiaoyv.script.database.dao.RakuenDao
import org.jsoup.Jsoup
import java.net.Proxy
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

object BgmScript {
    private var total = AtomicInteger(0)
    private var lastTotal = AtomicInteger(0)
    private val stack = CopyOnWriteArrayList<Float>()

    @JvmStatic
    fun main(args: Array<String>) {
        println("Bgm 数据同步脚本！")

        // 查询最新的 ID
        val newTopicId = queryBgmNewTopicId()
        println("当前班固米最新的 TopicId = $newTopicId")

        // 构建ID集合
        println("检查待更新ID数据集合")
        val idList = arrayListOf<Long>()
        for (id in 1..newTopicId) {
            if (!BgmScriptDataBase.instance.isIdExist("bgm_rakuen", id)) {
                idList.add(id)
            }
        }

        if (idList.size == 0) {
            println("当前超展开数据库已经同步到了最新")
            return
        }


        total.set(idList.size)

        println("本次待更新数据：$total 条")
        val itemCount = idList.size / 25
        idList.chunked(if (itemCount == 0) idList.size else itemCount).forEachIndexed { _, longs ->
            thread { startTask(longs, Proxy.NO_PROXY) }
        }

        thread {
            while (total.get() != 0) {
                val lastTotal = lastTotal.get()
                val speed = lastTotal - total.get()
                if (speed != 0) {
                    val secs = total.get() / speed.toFloat() / 60f
                    if (stack.size >= 10) {
                        stack.removeAt(0)
                        stack.add(secs)
                    } else {
                        stack.add(secs)
                    }
                    println(String.format("预计耗时：%.2f min", stack.average()))
                }
                this.lastTotal.set(total.get())
                Thread.sleep(1000)
            }
        }
    }

    private fun startTask(idList: List<Long>, proxy: Proxy) {
        idList.forEach { id ->
            val entity = requestTopic(id, proxy)
            val remainder = total.decrementAndGet()

            if (entity != null) {
                BgmScriptDataBase.instance.saveRakuen(entity)
                println(
                    String.format(
                        "完成：%6d，剩余：%6d，内容：%s",
                        id,
                        remainder,
                        entity.title.ifBlank { entity.error })
                )
            } else {
                System.err.println("错误：$id，剩余：$remainder")
            }
        }
    }

    private fun requestTopic(id: Long, proxy: Proxy): RakuenDao? {
        val result = runCatching {
            Jsoup.connect("https://bgm.tv/rakuen/topic/group/$id")
                .proxy(proxy)
                .timeout(120 * 1000)
                .get()
        }

        if (result.isFailure) {
            result.exceptionOrNull()?.printStackTrace()
            return null
        } else {
            val document = result.getOrThrow()
            val subjectInfo = document.select("#subject_info")
            val h1 = subjectInfo.select("#pageHeader h1")
            val h1Span = h1.select("span").remove()
            val grpId = h1Span.select("a.avatar").attr("href").substringAfterLast("/")
            val grpName = h1Span.select("a.avatar").text()
            val grpAvatar = h1Span.select("img").attr("src").optImageUrl()
            val postTopic = document.select(".postTopic")
            val time = postTopic.select(".post_actions small").text()
                .replace("#\\d+\\s*-".toRegex(), "")
                .trim()
            val userId = postTopic.select("a.avatar").attr("href").substringAfterLast("/")
            val userAvatar = postTopic.select("a.avatar > span").styleBackground().optImageUrl()
            val userName = postTopic.select(".inner > strong").text()
            val content = postTopic.select(".topic_content")
            val summary = content.text().trim()
            val image = content.html().parserImage()

            // 错误
            val columnNotice = document.select("#colunmNotice")
            val error = if (columnNotice.isNotEmpty()) {
                columnNotice.select(".text").text()
            } else {
                null
            }

            return RakuenDao(
                id = id,
                error = error,
                title = h1.text().trim(),
                summary = summary,
                uid = userId,
                user_name = userName,
                user_avatar = userAvatar,
                image = image,
                time = time,
                bgm_grp = grpId,
                bgm_grp_name = grpName,
                bgm_grp_avatar = grpAvatar,
            )
        }
    }
}

private fun queryBgmNewTopicId(): Long {
    val document = Jsoup.connect("https://bgm.tv/rakuen/topiclist?type=group")
        .timeout(120 * 1000)
        .get()

    return document.select("#eden_tpc_list > ul > li")
        .map { it.select("a.title").attr("href") }
        .filter { it.contains("group") }
        .maxOf {
            it.substringAfterLast("/").toLongOrNull() ?: 0
        }
}
