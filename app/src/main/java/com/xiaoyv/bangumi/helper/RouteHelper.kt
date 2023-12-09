package com.xiaoyv.bangumi.helper

import androidx.core.os.bundleOf
import com.blankj.utilcode.util.ActivityUtils
import com.xiaoyv.bangumi.ui.HomeActivity
import com.xiaoyv.bangumi.ui.discover.blog.detail.BlogActivity
import com.xiaoyv.bangumi.ui.discover.group.detail.GroupDetailActivity
import com.xiaoyv.bangumi.ui.discover.group.topic.GroupTopicsActivity
import com.xiaoyv.bangumi.ui.feature.calendar.CalendarActivity
import com.xiaoyv.bangumi.ui.feature.login.LoginActivity
import com.xiaoyv.bangumi.ui.feature.message.MessageActivity
import com.xiaoyv.bangumi.ui.feature.message.detail.MessageDetailActivity
import com.xiaoyv.bangumi.ui.feature.musmme.MusumeActivity
import com.xiaoyv.bangumi.ui.feature.notify.NotifyActivity
import com.xiaoyv.bangumi.ui.feature.person.PersonActivity
import com.xiaoyv.bangumi.ui.feature.post.blog.PostBlogActivity
import com.xiaoyv.bangumi.ui.feature.post.topic.PostTopicActivity
import com.xiaoyv.bangumi.ui.feature.preview.image.PreviewImageActivity
import com.xiaoyv.bangumi.ui.feature.setting.SettingActivity
import com.xiaoyv.bangumi.ui.feature.topic.TopicActivity
import com.xiaoyv.bangumi.ui.feature.user.UserActivity
import com.xiaoyv.bangumi.ui.media.detail.MediaDetailActivity
import com.xiaoyv.bangumi.ui.profile.edit.EditProfileActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.open
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.config.annotation.TopicType
import com.xiaoyv.common.kts.debugLog

/**
 * Class: [RouteHelper]
 *
 * @author why
 * @since 11/25/23
 */
object RouteHelper {

    fun jumpCalendar() {
        ActivityUtils.startActivity(CalendarActivity::class.java)
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

    fun jumpPostBlog(mediaDetailEntity: MediaDetailEntity? = null) {
        ActivityUtils.startActivity(
            bundleOf(NavKey.KEY_PARCELABLE to mediaDetailEntity),
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

    }
    fun jumpTagDetail(tag: String) {

    }

    fun jumpGroupDetail(groupId: String) {
        GroupDetailActivity::class.open(bundleOf(NavKey.KEY_STRING to groupId))
    }

    fun jumpGroupTopics(groupId: String) {
        GroupTopicsActivity::class.open(bundleOf(NavKey.KEY_STRING to groupId))
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
     * - https://bangumi.tv/group/topic/390252#post_2535628
     */
    fun handleUrl(titleLink: String) {
        debugLog { "Handle Url: $titleLink" }
    }
}