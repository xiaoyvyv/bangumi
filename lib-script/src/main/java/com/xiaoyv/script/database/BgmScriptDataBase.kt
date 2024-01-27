package com.xiaoyv.script.database

import com.xiaoyv.script.database.dao.IndexDao
import com.xiaoyv.script.database.dao.RakuenDao
import com.xiaoyv.script.database.dao.SubjectDao
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager


/**
 * Class: [BgmScriptDataBase]
 *
 * @author why
 * @since 1/14/24
 */
class BgmScriptDataBase private constructor() {
    private val rootDir = System.getProperty("user.dir")
    private val dataDir = "$rootDir/lib-script/bgm"
    private var _database: Connection? = null

    private val localUrl: String
        get() {
            val db = "$rootDir/GroupTopic.db"
            return if (File(db).exists()) "jdbc:sqlite:$db"
            else "jdbc:sqlite:$dataDir/GroupTopic.db"
        }

    private val database: Connection
        get() = instance.getOrCreateDatabase()

    private fun getOrCreateDatabase(): Connection {
        if (_database?.isClosed == true) {
            _database = null
        }
        Files.createDirectories(Paths.get(dataDir))
        return _database ?: DriverManager.getConnection(localUrl).also {
            _database = it
        }
    }

    private fun close() {
        runCatching { _database?.close() }
        _database = null
    }

    fun isIdExist(table: String, id: Long): Boolean {
        return synchronized(this) {
            database.prepareStatement("SELECT EXISTS(SELECT id FROM $table WHERE id = $id) AS id_exists;")
                .use { statement ->
                    statement.executeQuery().use { set ->
                        set.getInt("id_exists") != 0
                    }
                }
        }
    }

    fun saveSubject(entity: SubjectDao) {
        synchronized(this) {
            val id = SubjectDao::id
            val name = SubjectDao::name
            val name_cn = SubjectDao::name_cn
            val nsfw = SubjectDao::nsfw
            val type = SubjectDao::type

            val fields = mapOf(
                id to entity.id,
                name to entity.name,
                name_cn to entity.name_cn,
                nsfw to entity.nsfw,
                type to entity.type
            )

            val insertSQL = buildString {
                append("INSERT INTO bgm_subject (")
                append(fields.keys.joinToString(", ") { it.name })
                append(") VALUES (")
                append(fields.keys.joinToString(", ") { "?" })
                append(")")
            }
            database.prepareStatement(insertSQL).apply {
                fields.keys.forEachIndexed { index, it ->
                    when (val get = it.get(entity)) {
                        is String? -> setString(index + 1, get)
                        is Long -> setLong(index + 1, get)
                        is Int -> setInt(index + 1, get)
                        is Boolean -> setBoolean(index + 1, get)
                        else -> setString(index + 1, get.toString())
                    }
                }
            }.executeUpdate()
        }
    }

    fun saveIndex(entity: IndexDao) {
        synchronized(this) {
            val id = IndexDao::id
            val desc = IndexDao::desc
            val title = IndexDao::title
            val nickname = IndexDao::nickname
            val username = IndexDao::username
            val ban = IndexDao::ban
            val nsfw = IndexDao::nsfw
            val total = IndexDao::total
            val collects = IndexDao::collects
            val comments = IndexDao::comments
            val createdAt = IndexDao::createdAt
            val updatedAt = IndexDao::updatedAt
            val error = IndexDao::error

            val fields = mapOf(
                id to entity.id,
                title to entity.title,
                desc to entity.desc,
                nickname to entity.nickname,
                username to entity.username,
                ban to entity.ban,
                nsfw to entity.nsfw,
                total to entity.total,
                collects to entity.collects,
                comments to entity.comments,
                createdAt to entity.createdAt,
                updatedAt to entity.updatedAt,
                error to entity.error,
            )

            val insertSQL = buildString {
                append("INSERT INTO bgm_index (")
                append(fields.keys.joinToString(", ") { it.name })
                append(") VALUES (")
                append(fields.keys.joinToString(", ") { "?" })
                append(")")
            }
            database.prepareStatement(insertSQL).apply {
                fields.keys.forEachIndexed { index, it ->
                    when (val get = it.get(entity)) {
                        is String? -> setString(index + 1, get)
                        is Long -> setLong(index + 1, get)
                        is Int -> setInt(index + 1, get)
                        is Boolean -> setBoolean(index + 1, get)
                        else -> setString(index + 1, get.toString())
                    }
                }
            }.executeUpdate()
        }
    }

    fun saveRakuen(entity: RakuenDao) {
        synchronized(this) {
            val id = RakuenDao::id
            val title = RakuenDao::title
            val summary = RakuenDao::summary
            val uid = RakuenDao::uid
            val user_name = RakuenDao::user_name
            val user_avatar = RakuenDao::user_avatar
            val image = RakuenDao::image
            val time = RakuenDao::time
            val bgm_grp = RakuenDao::bgm_grp
            val bgm_grp_name = RakuenDao::bgm_grp_name
            val bgm_grp_avatar = RakuenDao::bgm_grp_avatar
            val error = RakuenDao::error

            val fields = mapOf(
                id to entity.id,
                title to entity.title,
                summary to entity.summary,
                uid to entity.uid,
                user_name to entity.user_name,
                user_avatar to entity.user_avatar,
                image to entity.image,
                time to entity.time,
                bgm_grp to entity.bgm_grp,
                bgm_grp_name to entity.bgm_grp_name,
                bgm_grp_avatar to entity.bgm_grp_avatar,
                error to entity.error?.ifEmpty { null },
            )

            val insertSQL = buildString {
                append("INSERT INTO bgm_rakuen (")
                append(fields.keys.joinToString(", ") { it.name })
                append(") VALUES (")
                append(fields.keys.joinToString(", ") { "?" })
                append(")")
            }
            database.prepareStatement(insertSQL).apply {
                fields.keys.forEachIndexed { index, it ->
                    when (val get = it.get(entity)) {
                        is String? -> setString(index + 1, get)
                        is Long -> setLong(index + 1, get)
                        is Int -> setInt(index + 1, get)
                        is Boolean -> setBoolean(index + 1, get)
                        else -> setString(index + 1, get.toString())
                    }
                }
            }.executeUpdate()
        }
    }

    companion object {
        val instance by lazy { BgmScriptDataBase() }

        fun close() {
            instance.close()
        }
    }
}