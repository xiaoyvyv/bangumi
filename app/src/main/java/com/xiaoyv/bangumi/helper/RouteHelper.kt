package com.xiaoyv.bangumi.helper

import android.content.Intent
import android.webkit.URLUtil
import androidx.activity.result.ActivityResultLauncher
import androidx.core.os.bundleOf
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.Utils
import com.xiaoyv.bangumi.special.picture.AnimePicturesNetActivity
import com.xiaoyv.bangumi.ui.HomeActivity
import com.xiaoyv.bangumi.ui.discover.blog.detail.BlogActivity
import com.xiaoyv.bangumi.ui.discover.group.detail.GroupDetailActivity
import com.xiaoyv.bangumi.ui.discover.group.list.GroupListActivity
import com.xiaoyv.bangumi.ui.discover.group.topic.GroupTopicsActivity
import com.xiaoyv.bangumi.ui.discover.index.detail.IndexDetailActivity
import com.xiaoyv.bangumi.ui.discover.index.list.IndexListActivity
import com.xiaoyv.bangumi.ui.feature.almanac.AlmanacActivity
import com.xiaoyv.bangumi.ui.feature.calendar.CalendarActivity
import com.xiaoyv.bangumi.ui.feature.login.LoginActivity
import com.xiaoyv.bangumi.ui.feature.magi.MagiActivity
import com.xiaoyv.bangumi.ui.feature.message.MessageActivity
import com.xiaoyv.bangumi.ui.feature.message.detail.MessageDetailActivity
import com.xiaoyv.bangumi.ui.feature.musmme.MusumeActivity
import com.xiaoyv.bangumi.ui.feature.notify.NotifyActivity
import com.xiaoyv.bangumi.ui.feature.person.PersonActivity
import com.xiaoyv.bangumi.ui.feature.post.blog.PostBlogActivity
import com.xiaoyv.bangumi.ui.feature.post.preview.PreviewBBCodeActivity
import com.xiaoyv.bangumi.ui.feature.post.topic.PostTopicActivity
import com.xiaoyv.bangumi.ui.feature.preview.image.PreviewImageActivity
import com.xiaoyv.bangumi.ui.feature.search.SearchActivity
import com.xiaoyv.bangumi.ui.feature.search.detail.SearchDetailActivity
import com.xiaoyv.bangumi.ui.feature.setting.SettingActivity
import com.xiaoyv.bangumi.ui.feature.setting.block.BlockActivity
import com.xiaoyv.bangumi.ui.feature.setting.privacy.PrivacyActivity
import com.xiaoyv.bangumi.ui.feature.setting.robot.RobotConfigActivity
import com.xiaoyv.bangumi.ui.feature.setting.translate.TranslateConfigActivity
import com.xiaoyv.bangumi.ui.feature.summary.SummaryActivity
import com.xiaoyv.bangumi.ui.feature.tag.TagDetailActivity
import com.xiaoyv.bangumi.ui.feature.topic.TopicActivity
import com.xiaoyv.bangumi.ui.feature.user.UserActivity
import com.xiaoyv.bangumi.ui.feature.user.blog.UserBlogActivity
import com.xiaoyv.bangumi.ui.feature.user.mono.UserMonoActivity
import com.xiaoyv.bangumi.ui.feature.web.WebActivity
import com.xiaoyv.bangumi.ui.media.detail.MediaDetailActivity
import com.xiaoyv.bangumi.ui.profile.edit.EditProfileActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.open
import com.xiaoyv.common.api.parser.parseCount
import com.xiaoyv.common.config.GlobalConfig
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.config.annotation.TopicType
import com.xiaoyv.common.config.bean.PostAttach
import com.xiaoyv.common.config.bean.SearchItem
import com.xiaoyv.common.helper.CacheHelper
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.decodeUrl
import com.xiaoyv.widget.kts.showToastCompat

/**
 * Class: [RouteHelper]
 *
 * @author why
 * @since 11/25/23
 */
object RouteHelper {
    /**
     * - https://bangumi.tv/group/topic/390252#post_2535628
     * - /person/57315
     */
    fun handleUrl(titleLink: String): Boolean {
        val id = titleLink.substringAfterLast("/")
            .substringBefore("#")
            .substringBefore("?")

        debugLog { "Handle Url: $titleLink" }

        if (URLUtil.isNetworkUrl(titleLink) && !titleLink.contains("bgm") && !titleLink.contains("bangumi")) {
            jumpWeb(titleLink, fitToolbar = true)
            return true
        }

        when {
            // 话题
            titleLink.contains(BgmPathType.TYPE_TOPIC) -> {
                when {
                    // 虚拟人物
                    titleLink.contains(TopicType.TYPE_CRT) -> {
                        jumpTopicDetail(id, TopicType.TYPE_CRT)
                        return true
                    }
                    // 章节
                    titleLink.contains(TopicType.TYPE_EP) -> {
                        jumpTopicDetail(id, TopicType.TYPE_EP)
                        return true
                    }
                    // 小组
                    titleLink.contains(TopicType.TYPE_GROUP) -> {
                        jumpTopicDetail(id, TopicType.TYPE_GROUP)
                        return true
                    }
                    // 现实人物
                    titleLink.contains(TopicType.TYPE_PERSON) -> {
                        jumpTopicDetail(id, TopicType.TYPE_PERSON)
                        return true
                    }
                    // 条目
                    titleLink.contains(TopicType.TYPE_SUBJECT) -> {
                        jumpTopicDetail(id, TopicType.TYPE_SUBJECT)
                        return true
                    }
                }
            }
            // 日志
            titleLink.contains(BgmPathType.TYPE_BLOG) -> {
                jumpBlogDetail(id)
                return true
            }
            // 标签
            titleLink.contains(BgmPathType.TYPE_SEARCH_TAG) -> {
                val decodeUrl = titleLink.decodeUrl()
                val tag = decodeUrl.substringAfter("tag").trim('/')
                    .substringBefore("/")
                val mediaType = decodeUrl.substringBefore("tag").trim('/')
                    .substringAfterLast("/")
                jumpTagDetail(mediaType, tag)
                return true
            }
            // 虚拟角色
            titleLink.contains(BgmPathType.TYPE_CHARACTER) -> {
                jumpPerson(id, true)
                return true
            }
            // 现实人物
            titleLink.contains(BgmPathType.TYPE_PERSON) -> {
                jumpPerson(titleLink.parseCount().toString(), false)
                return true
            }
            // 用户
            titleLink.contains(BgmPathType.TYPE_USER) -> {
                jumpUserDetail(id)
                return true
            }
            // 条目
            titleLink.contains(BgmPathType.TYPE_SUBJECT) -> {
                jumpMediaDetail(id)
                return true
            }
            // 小组
            titleLink.contains(BgmPathType.TYPE_GROUP) -> {
                jumpGroupDetail(id)
                return true
            }
            // 目录
            titleLink.contains(BgmPathType.TYPE_INDEX) -> {
                jumpIndexDetail(id)
                return true
            }
        }

        return false
    }

    fun jumpCalendar(isToday: Boolean) {
        ActivityUtils.startActivity(
            bundleOf(NavKey.KEY_BOOLEAN to isToday),
            CalendarActivity::class.java
        )
    }

    fun jumpLogin() {
        ActivityUtils.startActivity(LoginActivity::class.java)
    }

    fun jumpRobot() {
        ActivityUtils.startActivity(MusumeActivity::class.java)
    }

    fun jumpHome() {
        ActivityUtils.startActivity(HomeActivity::class.java)
    }

    fun jumpEditProfile() {
        ActivityUtils.startActivity(EditProfileActivity::class.java)
    }

    fun jumpMediaDetail(mediaId: String, mediaType: String? = null) {
        MediaDetailActivity::class.open(
            bundleOf(
                NavKey.KEY_STRING to mediaId,
                NavKey.KEY_STRING_SECOND to mediaType
            )
        )
    }

    fun jumpBlogDetail(blogId: String) {
        BlogActivity::class.open(bundleOf(NavKey.KEY_STRING to blogId))
    }

    fun jumpPreviewImage(showImage: String, totalImage: List<String> = emptyList()) {
        ActivityUtils.startActivity(
            bundleOf(
                NavKey.KEY_STRING to showImage,
                NavKey.KEY_SERIALIZABLE_ARRAY to totalImage.toTypedArray(),
            ),
            PreviewImageActivity::class.java,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
    }

    /**
     * 跳转到发布页
     */
    fun jumpPostBlog(postAttach: PostAttach? = null) {
        ActivityUtils.startActivity(
            bundleOf(NavKey.KEY_PARCELABLE to postAttach),
            PostBlogActivity::class.java
        )
    }

    fun jumpEditBlog(blogId: String) {
        ActivityUtils.startActivity(
            bundleOf(
                // 编辑模式
                NavKey.KEY_BOOLEAN to true,
                NavKey.KEY_STRING to blogId
            ),
            PostBlogActivity::class.java
        )
    }

    fun jumpUserBlog(userId: String) {
        ActivityUtils.startActivity(
            bundleOf(NavKey.KEY_STRING to userId),
            UserBlogActivity::class.java
        )
    }

    fun jumpUserMono(userId: String) {
        ActivityUtils.startActivity(
            bundleOf(NavKey.KEY_STRING to userId),
            UserMonoActivity::class.java
        )
    }

    fun jumpPostTopic(postAttach: PostAttach? = null) {
        ActivityUtils.startActivity(
            bundleOf(NavKey.KEY_PARCELABLE to postAttach),
            PostTopicActivity::class.java
        )
    }

    fun jumpEditTopic(topicId: String) {
        ActivityUtils.startActivity(
            bundleOf(
                // 编辑模式
                NavKey.KEY_BOOLEAN to true,
                NavKey.KEY_STRING to topicId
            ),
            PostTopicActivity::class.java
        )
    }

    fun jumpTopicDetail(topicId: String, @TopicType topicType: String) {
        TopicActivity::class.open(
            bundleOf(
                NavKey.KEY_STRING to topicId,
                NavKey.KEY_STRING_SECOND to topicType
            )
        )
    }

    fun jumpUserDetail(userId: String) {
        UserActivity::class.open(bundleOf(NavKey.KEY_STRING to userId))
    }

    fun jumpPerson(personId: String, isVirtual: Boolean) {
        PersonActivity::class.open(
            bundleOf(
                NavKey.KEY_STRING to personId,
                NavKey.KEY_BOOLEAN to isVirtual,
            )
        )
    }

    fun jumpIndexDetail(id: String) {
        IndexDetailActivity::class.open(bundleOf(NavKey.KEY_STRING to id))
    }

    /**
     * 目录列表查询，没有指定用户则查询全部目录
     *
     * @param targetUserId 如果指定了用户ID，[isSortByNewest] 则表示是否查询用户创建的目录，否则查询收藏的目录
     */
    fun jumpIndexList(isSortByNewest: Boolean, targetUserId: String? = null) {
        IndexListActivity::class.open(
            bundleOf(
                NavKey.KEY_BOOLEAN to isSortByNewest,
                NavKey.KEY_STRING to targetUserId
            )
        )
    }

    fun jumpGroupList() {
        GroupListActivity::class.open()
    }

    /**
     * 标签详情页面
     */
    fun jumpTagDetail(@MediaType mediaType: String, tag: String) {
        TagDetailActivity::class.open(
            bundleOf(
                NavKey.KEY_STRING to mediaType,
                NavKey.KEY_STRING_SECOND to tag
            )
        )
    }

    fun jumpGroupDetail(groupId: String) {
        GroupDetailActivity::class.open(bundleOf(NavKey.KEY_STRING to groupId))
    }

    fun jumpGroupTopics(groupId: String) {
        GroupTopicsActivity::class.open(bundleOf(NavKey.KEY_STRING to groupId))
    }

    /**
     * 跳转到我的话题
     */
    fun jumpMyTopics(sendOrReply: Boolean) {
        val fakeId = if (sendOrReply) GlobalConfig.GROUP_MY_SEND_TOPIC
        else GlobalConfig.GROUP_MY_REPLY_TOPIC

        GroupTopicsActivity::class.open(bundleOf(NavKey.KEY_STRING to fakeId))
    }

    fun jumpNotify() {
        ActivityUtils.startActivity(NotifyActivity::class.java)
    }

    fun jumpMessage() {
        ActivityUtils.startActivity(MessageActivity::class.java)
    }


    fun jumpMessageDetail(messageId: String, fromName: String) {
        MessageDetailActivity::class.open(
            bundleOf(
                NavKey.KEY_STRING to messageId,
                NavKey.KEY_STRING_SECOND to fromName
            )
        )
    }

    fun jumpSetting() {
        ActivityUtils.startActivity(SettingActivity::class.java)
    }

    /**
     * @param forSelectMedia 是否为选取媒体条目
     */
    fun jumpSearch(forSelectMedia: Boolean = false) {
        ActivityUtils.startActivity(
            bundleOf(
                NavKey.KEY_BOOLEAN to forSelectMedia
            ), SearchActivity::class.java
        )
    }

    fun jumpSearchDetail(searchItem: SearchItem) {
        CacheHelper.saveSearchHistory(searchItem)

        SearchDetailActivity::class.open(
            bundleOf(NavKey.KEY_PARCELABLE to searchItem)
        )
    }

    fun jumpSearchDetailForSelectMedia(
        launcher: ActivityResultLauncher<Intent>,
        searchItem: SearchItem,
    ) {
        launcher.launch(Intent(Utils.getApp(), SearchDetailActivity::class.java).apply {
            putExtra(NavKey.KEY_PARCELABLE, searchItem)
        })
    }

    fun jumpSummaryDetail(vararg htmlSummary: String) {
        SummaryActivity::class.open(
            bundleOf(NavKey.KEY_SERIALIZABLE_ARRAY to htmlSummary)
        )
    }

    fun jumpRatingDetail() {

    }

    fun jumpWeb(
        url: String,
        fitToolbar: Boolean = true,
        smallToolbar: Boolean = false,
        injectJs: String = "",
    ) {
        WebActivity::class.open(
            bundleOf(
                NavKey.KEY_STRING to url,
                NavKey.KEY_STRING_SECOND to injectJs,
                NavKey.KEY_BOOLEAN to fitToolbar,
                NavKey.KEY_BOOLEAN_SECOND to smallToolbar
            )
        )
    }

    fun jumpAlmanac() {
        AlmanacActivity::class.open()
    }

    fun jumpMagi() {
        MagiActivity::class.open()
    }

    fun jumpSendMessage(userId: String) {
        showToastCompat("正在开发中...")
    }

    /**
     * 预览 BBCode
     */
    fun jumpPreviewBBCode(bbCode: String) {
        PreviewBBCodeActivity::class.open(bundleOf(NavKey.KEY_STRING to bbCode))
    }

    fun jumpBlockUser() {
        BlockActivity::class.open()
    }

    fun jumpPrivacy() {
        PrivacyActivity::class.open()
    }

    fun jumpTranslateConfig() {
        TranslateConfigActivity::class.open()
    }

    fun jumpRobotConfig() {
        RobotConfigActivity::class.open()
    }

    fun jumpAnimePictures() {
        AnimePicturesNetActivity::class.open()
    }
}