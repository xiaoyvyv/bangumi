package com.xiaoyv.bangumi.helper

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.core.os.bundleOf
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.Utils
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
import com.xiaoyv.bangumi.ui.feature.post.topic.PostTopicActivity
import com.xiaoyv.bangumi.ui.feature.preview.image.PreviewImageActivity
import com.xiaoyv.bangumi.ui.feature.search.SearchActivity
import com.xiaoyv.bangumi.ui.feature.search.detail.SearchDetailActivity
import com.xiaoyv.bangumi.ui.feature.setting.SettingActivity
import com.xiaoyv.bangumi.ui.feature.summary.SummaryActivity
import com.xiaoyv.bangumi.ui.feature.tag.TagDetailActivity
import com.xiaoyv.bangumi.ui.feature.topic.TopicActivity
import com.xiaoyv.bangumi.ui.feature.user.UserActivity
import com.xiaoyv.bangumi.ui.feature.web.WebActivity
import com.xiaoyv.bangumi.ui.media.detail.MediaDetailActivity
import com.xiaoyv.bangumi.ui.profile.edit.EditProfileActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.open
import com.xiaoyv.common.api.parser.parseCount
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.config.annotation.ReportType
import com.xiaoyv.common.config.annotation.TopicType
import com.xiaoyv.common.config.bean.PostAttach
import com.xiaoyv.common.config.bean.SearchItem
import com.xiaoyv.common.helper.CacheHelper
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.decodeUrl
import com.xiaoyv.common.widget.dialog.AnimeReportDialog

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
    fun jumpPostBlog(relateMedia: PostAttach? = null) {
        ActivityUtils.startActivity(
            bundleOf(NavKey.KEY_PARCELABLE to relateMedia),
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

    fun jumpPostTopic(groupId: String) {
        ActivityUtils.startActivity(
            bundleOf(NavKey.KEY_STRING to groupId),
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

    fun jumpIndexList(isSortByNewest: Boolean) {
        IndexListActivity::class.open(bundleOf(NavKey.KEY_BOOLEAN to isSortByNewest))
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
        val fakeId = if (sendOrReply) "my_topic" else "my_reply"
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

    fun jumpWeb(url: String, fitToolbar: Boolean = false, injectJs: String = "") {
        WebActivity::class.open(
            bundleOf(
                NavKey.KEY_STRING to url,
                NavKey.KEY_STRING_SECOND to injectJs,
                NavKey.KEY_BOOLEAN to fitToolbar
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

    }

    fun jumpReport(userId: String, @ReportType type: String) {
        AnimeReportDialog.show(userId, type)
    }

}