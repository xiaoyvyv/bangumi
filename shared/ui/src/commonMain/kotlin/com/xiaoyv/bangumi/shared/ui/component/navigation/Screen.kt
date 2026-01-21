package com.xiaoyv.bangumi.shared.ui.component.navigation

import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import com.xiaoyv.bangumi.shared.component.DetectType
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.RakuenIdType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.MonoBrowserBody
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.SubjectBrowserBody
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.core.module.KoinDslMarker
import org.koin.core.module.Module
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.scope.Scope

val Scope.navigator get() = get<Navigator>()

val NavKeyScopeArchetype = TypeQualifier(NavKey::class)

@KoinDslMarker
inline fun Module.navScope(scopeSet: Module.() -> Unit) {
    scopeSet()
}

/**
 * Creates the required serializing configuration for open polymorphism
 */
val stateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(Screen.Empty::class, Screen.Empty.serializer())
            subclass(Screen.Splash::class, Screen.Splash.serializer())
            subclass(Screen.Main::class, Screen.Main.serializer())
            subclass(Screen.SignIn::class, Screen.SignIn.serializer())
            subclass(Screen.Timeline::class, Screen.Timeline.serializer())
            subclass(Screen.Topic::class, Screen.Topic.serializer())
            subclass(Screen.Tracking::class, Screen.Tracking.serializer())
            subclass(Screen.Profile::class, Screen.Profile.serializer())
            subclass(Screen.Home::class, Screen.Home.serializer())
            subclass(Screen.Almanac::class, Screen.Almanac.serializer())
            subclass(Screen.SettingsMain::class, Screen.SettingsMain.serializer())
            subclass(Screen.SettingsAccount::class, Screen.SettingsAccount.serializer())
            subclass(Screen.SettingsBar::class, Screen.SettingsBar.serializer())
            subclass(Screen.SettingsBlock::class, Screen.SettingsBlock.serializer())
            subclass(Screen.SettingsLive2d::class, Screen.SettingsLive2d.serializer())
            subclass(Screen.SettingsNetwork::class, Screen.SettingsNetwork.serializer())
            subclass(Screen.SettingsPrivacy::class, Screen.SettingsPrivacy.serializer())
            subclass(Screen.SettingsTranslate::class, Screen.SettingsTranslate.serializer())
            subclass(Screen.SettingsUi::class, Screen.SettingsUi.serializer())
            subclass(Screen.Notification::class, Screen.Notification.serializer())
            subclass(Screen.MessageMain::class, Screen.MessageMain.serializer())
            subclass(Screen.MessageChat::class, Screen.MessageChat.serializer())
            subclass(Screen.Newest::class, Screen.Newest.serializer())
            subclass(Screen.Dollars::class, Screen.Dollars.serializer())
            subclass(Screen.UserDetail::class, Screen.UserDetail.serializer())
            subclass(Screen.GroupDetail::class, Screen.GroupDetail.serializer())
            subclass(Screen.IndexDetail::class, Screen.IndexDetail.serializer())
            subclass(Screen.TagDetail::class, Screen.TagDetail.serializer())
            subclass(Screen.PixivMain::class, Screen.PixivMain.serializer())
            subclass(Screen.PixivLogin::class, Screen.PixivLogin.serializer())
            subclass(Screen.TopicDetail::class, Screen.TopicDetail.serializer())
            subclass(Screen.Article::class, Screen.Article.serializer())
            subclass(Screen.DetectImage::class, Screen.DetectImage.serializer())
            subclass(Screen.SearchInput::class, Screen.SearchInput.serializer())
            subclass(Screen.SearchResult::class, Screen.SearchResult.serializer())
            subclass(Screen.MikanResources::class, Screen.MikanResources.serializer())
            subclass(Screen.MikanStudio::class, Screen.MikanStudio.serializer())
            subclass(Screen.Gallery::class, Screen.Gallery.serializer())
            subclass(Screen.PreviewText::class, Screen.PreviewText.serializer())
            subclass(Screen.Garden::class, Screen.Garden.serializer())
            subclass(Screen.SubjectDetail::class, Screen.SubjectDetail.serializer())
            subclass(Screen.SubjectBrowser::class, Screen.SubjectBrowser.serializer())
            subclass(Screen.PreviewMain::class, Screen.PreviewMain.serializer())
            subclass(Screen.MonoDetail::class, Screen.MonoDetail.serializer())
            subclass(Screen.MonoBrowser::class, Screen.MonoBrowser.serializer())
            subclass(Screen.Web::class, Screen.Web.serializer())
            subclass(Screen.TimelineDetail::class, Screen.TimelineDetail.serializer())
            subclass(Screen.Calendar::class, Screen.Calendar.serializer())
        }
    }
}


@Immutable
@Serializable
sealed class Screen(
    val route: String,
    val mode: LaunchMode = LaunchMode.DEFAULT,
    val stackAction: StackAction = StackAction.None
) : NavKey {
    @Serializable
    data object Empty : Screen("")

    @Serializable
    data object Splash : Screen(SCREEN_ROUTE_SPLASH)

    @Serializable
    data object Main : Screen(SCREEN_ROUTE_MAIN, LaunchMode.SINGLE_TASK, StackAction.ClearAll)

    @Serializable
    data object SignIn : Screen(SCREEN_ROUTE_SIGN_IN, LaunchMode.SINGLE_TOP)

    @Serializable
    data object Timeline : Screen(SCREEN_ROUTE_TIMELINE)

    @Serializable
    data object Topic : Screen(SCREEN_ROUTE_TOPIC)

    @Serializable
    data object Tracking : Screen(SCREEN_ROUTE_TRACKING)

    @Serializable
    data object Profile : Screen(SCREEN_ROUTE_PROFILE)

    @Serializable
    data object Home : Screen(SCREEN_ROUTE_HOME)

    @Serializable
    data object Almanac : Screen(SCREEN_ROUTE_ALMANAC)

    @Serializable
    data object SettingsMain : Screen(SCREEN_ROUTE_SETTINGS_MAIN)

    @Serializable
    data object SettingsAccount : Screen(SCREEN_ROUTE_SETTINGS_ACCOUNT)

    @Serializable
    data object SettingsBar : Screen(SCREEN_ROUTE_SETTINGS_BAR)

    @Serializable
    data object SettingsBlock : Screen(SCREEN_ROUTE_SETTINGS_BLOCK)

    @Serializable
    data object SettingsLive2d : Screen(SCREEN_ROUTE_SETTINGS_LIVE2D)

    @Serializable
    data object SettingsNetwork : Screen(SCREEN_ROUTE_SETTINGS_NETWORK)

    @Serializable
    data object SettingsPrivacy : Screen(SCREEN_ROUTE_SETTINGS_PRIVACY)

    @Serializable
    data object SettingsTranslate : Screen(SCREEN_ROUTE_SETTINGS_TRANSLATE)

    @Serializable
    data object SettingsUi : Screen(SCREEN_ROUTE_SETTINGS_UI)

    @Serializable
    data object Notification : Screen(SCREEN_ROUTE_NOTIFICATION)

    @Serializable
    data object MessageMain : Screen(SCREEN_ROUTE_MESSAGE_MAIN)

    @Serializable
    data class MessageChat(val id: Long, val username: String) : Screen(SCREEN_ROUTE_MESSAGE_CHAT)

    @Serializable
    data object Newest : Screen(SCREEN_ROUTE_NEWEST)

    @Serializable
    data object Dollars : Screen(SCREEN_ROUTE_DOLLARS)

    @Serializable
    data class UserDetail(val username: String) : Screen(SCREEN_ROUTE_USER_DETAIL)

    @Serializable
    data class GroupDetail(val name: String) : Screen(SCREEN_ROUTE_GROUP_DETAIL)

    @Serializable
    data class IndexDetail(val id: Long) : Screen(SCREEN_ROUTE_INDEX_DETAIL)

    @Serializable
    data class TagDetail(val type: Int = SubjectType.ANIME) : Screen(SCREEN_ROUTE_TAG_DETAIL)

    @Serializable
    data object PixivMain : Screen(SCREEN_ROUTE_PIXIV_MAIN)

    @Serializable
    data object PixivLogin : Screen(SCREEN_ROUTE_PIXIV_LOGIN)

    @Serializable
    data class TopicDetail(val id: Long, @field:RakuenIdType val type: String) : Screen(SCREEN_ROUTE_TOPIC)

    @Serializable
    data class Article(val id: Long, @field:RakuenIdType val type: String) : Screen(SCREEN_ROUTE_ARTICLE)

    @Serializable
    data class DetectImage(@field:DetectType val type: Int, val path: String = "") : Screen(SCREEN_ROUTE_RECEIVE)

    @Serializable
    data class SearchInput(val query: String = "") : Screen(SCREEN_ROUTE_SEARCH_INPUT)

    @Serializable
    data class SearchResult(val query: String) : Screen(SCREEN_ROUTE_SEARCH_RESULT)

    @Serializable
    data class MikanResources(val mikanId: String, val groupId: String, val groupName: String) :
        Screen(SCREEN_ROUTE_MIKAN_RESOURCES)

    @Serializable
    data class MikanStudio(val subjectId: Long, val mikanId: String) :
        Screen(SCREEN_ROUTE_MIKAN_STUDIO)

    @Serializable
    data class Gallery(val id: String, val type: Int) : Screen(SCREEN_ROUTE_GALLERY)

    @Serializable
    data class PreviewText(val text: String) : Screen(SCREEN_ROUTE_PREVIEW_TEXT)

    @Serializable
    data class Garden(val query: String = "") : Screen(SCREEN_ROUTE_GARDEN)

    @Serializable
    data class SubjectDetail(val subjectId: Long) : Screen(SCREEN_ROUTE_SUBJECT)

    @Serializable
    data class SubjectBrowser(
        val body: SubjectBrowserBody = SubjectBrowserBody.Empty,
        val title: String = "",
    ) : Screen(SCREEN_ROUTE_SUBJECT_BROWSER)

    @Serializable
    data class PreviewMain(val index: Int, val items: List<String>) : Screen(SCREEN_ROUTE_PREVIEW_MAIN) {
        constructor(url: String) : this(0, listOf(url))
    }

    @Serializable
    data class MonoDetail(val id: Long, @field:MonoType val type: Int) : Screen(SCREEN_ROUTE_MONO)

    @Serializable
    data class MonoBrowser(@field:MonoType val type: Int, val param: MonoBrowserBody = MonoBrowserBody.Empty) :
        Screen(SCREEN_ROUTE_MONO_BROWSER)

    @Serializable
    data class Web(val url: String) : Screen(SCREEN_ROUTE_WEB)

    @Serializable
    data class TimelineDetail(val id: Long) : Screen(SCREEN_ROUTE_TIMELINE_DETAIL)

    @Serializable
    data class Calendar(val isToday: Boolean) : Screen(SCREEN_ROUTE_CALENDAR)
}