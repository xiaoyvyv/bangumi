package com.xiaoyv.common.helper

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.EncodeUtils
import com.blankj.utilcode.util.StringUtils
import com.xiaoyv.blueprint.entity.LoadingState
import com.xiaoyv.blueprint.kts.launchProcess
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.emptyImageGetter
import com.xiaoyv.common.api.parser.entity.BlogDetailEntity
import com.xiaoyv.common.api.parser.entity.TopicDetailEntity
import com.xiaoyv.common.api.parser.parseHtml
import com.xiaoyv.common.api.parser.parserImage
import com.xiaoyv.common.api.request.GithubUpdateParam
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.LocalCollectionType
import com.xiaoyv.common.config.annotation.TopicType
import com.xiaoyv.common.database.BgmDatabaseManager
import com.xiaoyv.common.database.collection.Collection
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.fromJson
import com.xiaoyv.common.kts.i18n
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.showToastCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

/**
 * Class: [CollectionHelper]
 *
 * @author why
 * @since 1/3/24
 */
object CollectionHelper {
    /**
     * 配置数据
     */
    private val authorization get() = ConfigHelper.githubToken.trim()
    private val user get() = ConfigHelper.githubUser.trim()
    private val repo get() = ConfigHelper.githubRepo.trim()

    /**
     * 是否可以使用
     */
    val isEnable: Boolean
        get() = authorization.isNotBlank() && user.isNotBlank() && repo.isNotBlank()

    suspend fun isCollected(tid: String, @LocalCollectionType type: Int): Boolean {
        return withContext(Dispatchers.IO) {
            BgmDatabaseManager.collection.isCollected(tid, type)
        }
    }

    suspend fun saveBlog(entity: BlogDetailEntity) {
        withContext(Dispatchers.IO) {
            BgmDatabaseManager.collection.insertAll(
                Collection(
                    uid = UserHelper.currentUser.id,
                    tid = entity.id,
                    title = entity.title,
                    content = entity.content.substringContent(),
                    type = LocalCollectionType.TYPE_BLOG,
                    tUid = entity.userId,
                    tName = entity.userName,
                    tAvatar = entity.userAvatar,
                    tImage = entity.content.parserImage(),
                    tUrl = BgmApiManager.buildReferer(BgmPathType.TYPE_BLOG, entity.id)
                )
            )
        }

        showToastCompat(StringUtils.getString(CommonString.collect_blog_success))
    }

    suspend fun saveTopic(entity: TopicDetailEntity, @TopicType topicType: String) {
        withContext(Dispatchers.IO) {
            BgmDatabaseManager.collection.insertAll(
                Collection(
                    uid = UserHelper.currentUser.id,
                    tid = entity.id,
                    title = entity.title,
                    content = entity.content.substringContent().ifBlank {
                        TopicType.string(topicType)
                    },
                    type = LocalCollectionType.TYPE_TOPIC,
                    tUid = entity.userId,
                    tName = entity.userName,
                    tAvatar = entity.userAvatar,
                    tImage = entity.content.parserImage(),
                    tUrl = BgmApiManager.buildTopicUrl(entity.id, topicType)
                )
            )
        }

        showToastCompat(StringUtils.getString(CommonString.collect_topic_success))
    }

    suspend fun deleteCollect(tid: String, @LocalCollectionType type: Int) {
        withContext(Dispatchers.IO) {
            BgmDatabaseManager.collection.delete(tid, type)
        }

        showToastCompat(StringUtils.getString(CommonString.collect_cancel))
    }

    /**
     * 清空收藏数据
     */
    fun clearAll() {
        launchProcess {
            withContext(Dispatchers.IO) {
                BgmDatabaseManager.collection.deleteAll()
            }
        }
    }

    /**
     * 同步收藏数据
     *
     * @param overrideRemote 是否将远程数据强制和本地同步，注意如果本地为空，将会清空远程数据
     */
    fun syncCollection(
        state: MutableLiveData<LoadingState>? = null,
        overrideRemote: Boolean,
        toast: Boolean = true,
    ) {
        launchProcess(
            state = state,
            error = {
                it.printStackTrace()
                if (it is HttpException) {
                    if (it.response()?.code() == 401) {
                        if (toast) showToastCompat(i18n(CommonString.collect_token_error))
                        return@launchProcess
                    }
                }
                if (toast) showToastCompat(it.errorMsg)
            },
            block = {
                require(isEnable) { i18n(CommonString.collect_config_github) }

                val oldData = withContext(Dispatchers.IO) {
                    runCatching {
                        BgmApiManager.bgmJsonApi.queryGithubFileContent(
                            authorization = "Bearer $authorization",
                            user = user,
                            repo = repo,
                            path = "data/collection.json"
                        )
                    }.onFailure { it.printStackTrace() }.getOrNull()
                }

                val oldCollections = oldData?.content
                    .orEmpty()
                    .let { EncodeUtils.base64Decode(it).decodeToString() }
                    .fromJson<List<Collection>>()
                    .orEmpty()

                withContext(Dispatchers.IO) {
                    val data = mergeIntoLocal(oldCollections, overrideRemote).toJson().let {
                        EncodeUtils.base64Encode2String(it.encodeToByteArray())
                    }

                    BgmApiManager.bgmJsonApi.putGithubFileContent(
                        authorization = "Bearer $authorization",
                        user = user,
                        repo = repo,
                        path = "data/collection.json",
                        param = GithubUpdateParam(
                            message = i18n(CommonString.collect_update_commit),
                            committer = GithubUpdateParam.Committer(
                                name = i18n(CommonString.app_name),
                                email = UserHelper.cacheEmail
                            ),
                            content = data,
                            sha = oldData?.sha
                        )
                    )
                }

                if (toast) showToastCompat(i18n(CommonString.collect_sync_success))
            }
        )
    }

    /**
     * 合并数据
     *
     * @param skipInsetLocal 是否跳过插入本地不存在的数据
     */
    fun mergeIntoLocal(
        netCollections: List<Collection>,
        skipInsetLocal: Boolean,
    ): List<Collection> {
        // 覆盖远程数据，直接以本地数据为准
        if (skipInsetLocal) {
            return BgmDatabaseManager.collection.getAll()
        }

        // 合并数据
        netCollections.forEach {
            if (BgmDatabaseManager.collection.isCollected(it.tid, it.type)) {
                return@forEach
            }
            // reset id
            it.id = 0

            // insert
            BgmDatabaseManager.collection.insertAll(it)
        }

        // 读取本地全部
        return BgmDatabaseManager.collection.getAll()
    }


    /**
     * 限制长度
     */
    private fun String.substringContent(): String {
        if (isEmpty()) return this
        return parseHtml(imageGetter = emptyImageGetter).toString().trim().let {
            if (it.length > 300) it.substring(0, 300) else it
        }
    }
}
