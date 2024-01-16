package com.xiaoyv.script

import com.google.gson.Gson
import com.xiaoyv.script.database.BgmScriptDataBase
import com.xiaoyv.script.database.dao.IndexDao
import com.xiaoyv.script.entity.BgmScriptIndexEntity
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

/**
 * Class: [IndexScript]
 *
 * @author why
 * @since 1/15/24
 */
object IndexScript {
    private var total = AtomicInteger(0)

    @JvmStatic
    fun main(args: Array<String>) {
        val bgmNewIndexId = queryBgmNewIndexId()
        println("当前班固米最新的 IndexId = $bgmNewIndexId")

        // 构建ID集合
        println("检查待更新ID数据集合")
        val idList = arrayListOf<Long>()
        for (id in 1..bgmNewIndexId) {
            if (!BgmScriptDataBase.instance.isIdExist("bgm_index", id)) {
                idList.add(id)
            }
        }

        if (idList.size == 0) {
            println("当前目录数据库已经同步到了最新")
            return
        }

        total.set(idList.size)

        println("本次待更新数据：${total} 条")
        val itemCount = idList.size / 20
        idList.chunked(if (itemCount == 0) idList.size else itemCount).forEach {
            thread { startTask(it) }
        }
    }

    private fun startTask(idList: List<Long>) {
        idList.forEach { id ->
            val result = runCatching {
                val text = Jsoup.connect("https://api.bgm.tv/v0/indices/$id")
                    .timeout(120 * 1000)
                    .ignoreContentType(true)
                    .header("Authorization", "Bearer m6L2SwfeXrMiCnkYSDBr7XnapS18BAAO4RUDGMrt")
                    .get()
                    .text()
                Gson().fromJson(text, BgmScriptIndexEntity::class.java).let {
                    IndexDao(
                        id = it.id,
                        desc = it.desc,
                        title = it.title,
                        nickname = it.creator?.nickname.orEmpty(),
                        username = it.creator?.username.orEmpty(),
                        ban = if (it.ban) 1 else 0,
                        nsfw = if (it.nsfw) 1 else 0,
                        total = it.total,
                        collects = it.stat?.collects ?: 0,
                        comments = it.stat?.comments ?: 0,
                        createdAt = it.createdAt?.time ?: 0,
                        updatedAt = it.updatedAt?.time ?: 0,
                    )
                }
            }
            val get = total.decrementAndGet()
            val entity = result.getOrNull()
            val error = result.exceptionOrNull()

            if (entity == null) {
                if (error is HttpStatusException) {
                    BgmScriptDataBase.instance.saveIndex(
                        IndexDao(id, error = "HTTP: " + error.statusCode)
                    )
                    System.err.println("错误：$id，${error.message}")
                }
            } else {
                BgmScriptDataBase.instance.saveIndex(entity)
                println(String.format("ID：%6d -> %12s，剩余：%d", id, entity.title.orEmpty(), get))
            }
        }
    }

    private fun queryBgmNewIndexId(): Long {
        val document = Jsoup.connect("https://bgm.tv/index")
            .timeout(120 * 1000)
            .get()

        return document.select("#timeline").first()
            ?.select("h3 a")
            ?.attr("href")
            ?.substringAfterLast("/")
            ?.toLongOrNull() ?: 0
    }
}