package com.xiaoyv.bangumi.shared.ui.component.navigation

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.component.DetectType
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.RakuenIdType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.utils.RouteDefinition
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.MonoBrowserBody
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.SubjectBrowserBody


@Immutable
sealed class Screen(val route: String) {
    data object Empty : Screen("")
    data object Splash : Screen(SCREEN_ROUTE_SPLASH)
    data object Main : Screen(SCREEN_ROUTE_MAIN)
    data object SignIn : Screen(SCREEN_ROUTE_SIGN_IN)
    data object Timeline : Screen(SCREEN_ROUTE_TIMELINE)
    data object Topic : Screen(SCREEN_ROUTE_TOPIC)
    data object Tracking : Screen(SCREEN_ROUTE_TRACKING)
    data object Profile : Screen(SCREEN_ROUTE_PROFILE)
    data object Home : Screen(SCREEN_ROUTE_HOME)
    data object Almanac : Screen(SCREEN_ROUTE_ALMANAC)
    data object SettingsMain : Screen(SCREEN_ROUTE_SETTINGS_MAIN)
    data object SettingsAccount : Screen(SCREEN_ROUTE_SETTINGS_ACCOUNT)
    data object SettingsBar : Screen(SCREEN_ROUTE_SETTINGS_BAR)
    data object SettingsBlock : Screen(SCREEN_ROUTE_SETTINGS_BLOCK)
    data object SettingsLive2d : Screen(SCREEN_ROUTE_SETTINGS_LIVE2D)
    data object SettingsNetwork : Screen(SCREEN_ROUTE_SETTINGS_NETWORK)
    data object SettingsPrivacy : Screen(SCREEN_ROUTE_SETTINGS_PRIVACY)
    data object SettingsTranslate : Screen(SCREEN_ROUTE_SETTINGS_TRANSLATE)
    data object SettingsUi : Screen(SCREEN_ROUTE_SETTINGS_UI)
    data object Notification : Screen(SCREEN_ROUTE_NOTIFICATION)
    data object MessageMain : Screen(SCREEN_ROUTE_MESSAGE_MAIN)
    data class MessageChat(val id: Long, val username: String) : Screen(SCREEN_ROUTE_MESSAGE_CHAT)
    data object Newest : Screen(SCREEN_ROUTE_NEWEST)
    data object Dollars : Screen(SCREEN_ROUTE_DOLLARS)
    data class BlogList(val username: String) : Screen(SCREEN_ROUTE_BLOG_LIST)
    data class UserDetail(val username: String) : Screen(SCREEN_ROUTE_USER_DETAIL)
    data class GroupDetail(val name: String) : Screen(SCREEN_ROUTE_GROUP_DETAIL)
    data class IndexDetail(val id: Long) : Screen(SCREEN_ROUTE_INDEX_DETAIL)
    data class TagDetail(val type: Int = SubjectType.ANIME) : Screen(SCREEN_ROUTE_TAG_DETAIL)
    data object PixivMain : Screen(SCREEN_ROUTE_PIXIV_MAIN)
    data object PixivLogin : Screen(SCREEN_ROUTE_PIXIV_LOGIN)

    data class TopicDetail(val id: Long, @field:RakuenIdType val type: String) : Screen(SCREEN_ROUTE_TOPIC)
    data class Article(val id: Long, @field:RakuenIdType val type: String) : Screen(SCREEN_ROUTE_ARTICLE)
    data class DetectImage(@field:DetectType val type: Int, val path: String? = null) : Screen(SCREEN_ROUTE_RECEIVE)

    data class SearchInput(val query: String = "") : Screen(SCREEN_ROUTE_SEARCH_INPUT)
    data class SearchResult(val query: String) : Screen(SCREEN_ROUTE_SEARCH_RESULT)
    data class MikanResources(val mikanId: String, val groupId: String, val groupName: String) :
        Screen(SCREEN_ROUTE_MIKAN_RESOURCES)

    data class MikanStudio(val subjectId: Long, val mikanId: String) :
        Screen(SCREEN_ROUTE_MIKAN_STUDIO)

    data class Gallery(val id: String, val type: Int) : Screen(SCREEN_ROUTE_GALLERY)
    data class PreviewText(val text: String) : Screen(SCREEN_ROUTE_PREVIEW_TEXT)
    data class Garden(val query: String = "") : Screen(SCREEN_ROUTE_GARDEN)

    data class SubjectDetail(val subjectId: Long) : Screen(SCREEN_ROUTE_SUBJECT)
    data class SubjectBrowser(
        val body: SubjectBrowserBody = SubjectBrowserBody.Empty,
        val title: String = "",
    ) : Screen(SCREEN_ROUTE_SUBJECT_BROWSER)

    data class PreviewMain(val index: Int, val items: List<String>) : Screen(SCREEN_ROUTE_PREVIEW_MAIN) {
        constructor(url: String) : this(0, listOf(url))
    }

    data class MonoDetail(val id: Long, @field:MonoType val type: Int) : Screen(SCREEN_ROUTE_MONO)
    data class MonoBrowser(@field:MonoType val type: Int, val param: MonoBrowserBody = MonoBrowserBody.Empty) :
        Screen(SCREEN_ROUTE_MONO_BROWSER)

    data class Web(val url: String) : Screen(SCREEN_ROUTE_WEB)

    data class TimelineDetail(val id: Long) : Screen(SCREEN_ROUTE_TIMELINE_DETAIL)
    data class Calendar(val isToday: Boolean) : Screen(SCREEN_ROUTE_CALENDAR)

    companion object {
        val ArticleRouteDefinition = RouteDefinition(SCREEN_ROUTE_ARTICLE).apply {
            stringArg(EXTRA_TYPE)
            longArg(EXTRA_ID)
        }

        val MonoBrowserRouteDefinition = RouteDefinition(SCREEN_ROUTE_MONO_BROWSER).apply {
            intArg(EXTRA_TYPE)
            stringArg(EXTRA_OBJ)
        }

        val MessageChatRouteDefinition = RouteDefinition(SCREEN_ROUTE_MESSAGE_CHAT).apply {
            longArg(EXTRA_ID)
            stringArg(EXTRA_USERNAME)
        }

        val GroupDetailRouteDefinition = RouteDefinition(SCREEN_ROUTE_GROUP_DETAIL).apply {
            stringArg(EXTRA_NAME)
        }

        val TopicDetailRouteDefinition = RouteDefinition(SCREEN_ROUTE_TOPIC_DETAIL).apply {
            longArg(EXTRA_ID)
            intArg(EXTRA_TYPE)
        }

        val IndexDetailRouteDefinition = RouteDefinition(SCREEN_ROUTE_INDEX_DETAIL).apply {
            longArg(EXTRA_ID)
        }

        val PreviewTextRouteDefinition = RouteDefinition(SCREEN_ROUTE_PREVIEW_TEXT).apply {
            stringArg(EXTRA_TEXT)
        }

        val UserDetailRouteDefinition = RouteDefinition(SCREEN_ROUTE_USER_DETAIL).apply {
            stringArg(EXTRA_USERNAME)
        }

        val TimelineDetailRouteDefinition = RouteDefinition(SCREEN_ROUTE_TIMELINE_DETAIL).apply {
            longArg(EXTRA_ID)
        }

        val CalendarRouteDefinition = RouteDefinition(SCREEN_ROUTE_CALENDAR).apply {
            booleanArg(EXTRA_BOOLEAN)
        }

        val TagDetailRouteDefinition = RouteDefinition(SCREEN_ROUTE_TAG_DETAIL).apply {
            intArg(EXTRA_TYPE)
        }

        val SubjectDetailRouteDefinition = RouteDefinition(SCREEN_ROUTE_SUBJECT_BROWSER).apply {
            stringArg(EXTRA_OBJ)
            stringArg(EXTRA_TITLE)
        }

        val PreviewMainRouteDefinition = RouteDefinition(SCREEN_ROUTE_PREVIEW_MAIN).apply {
            intArg(EXTRA_INDEX)
            stringArg(EXTRA_OBJ)
        }

        val GardenRouteDefinition = RouteDefinition(SCREEN_ROUTE_GARDEN).apply {
            stringArg(EXTRA_TEXT)
        }
    }
}
