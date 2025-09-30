package com.xiaoyv.bangumi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.xiaoyv.bangumi.features.almanac.addAlmanacScreen
import com.xiaoyv.bangumi.features.almanac.navigateAlmanac
import com.xiaoyv.bangumi.features.article.addArticleScreen
import com.xiaoyv.bangumi.features.article.navigateArticle
import com.xiaoyv.bangumi.features.blog.page.addBlogPageScreen
import com.xiaoyv.bangumi.features.blog.page.navigateBlogPage
import com.xiaoyv.bangumi.features.detect.addReceiveScreen
import com.xiaoyv.bangumi.features.detect.navigateReceive
import com.xiaoyv.bangumi.features.dollars.addDollarsScreen
import com.xiaoyv.bangumi.features.dollars.navigateDollars
import com.xiaoyv.bangumi.features.gallery.addGalleryScreen
import com.xiaoyv.bangumi.features.gallery.navigateGallery
import com.xiaoyv.bangumi.features.garden.addGardenScreen
import com.xiaoyv.bangumi.features.garden.navigateGarden
import com.xiaoyv.bangumi.features.groups.detail.addGroupsDetailScreen
import com.xiaoyv.bangumi.features.groups.detail.navigateGroupsDetail
import com.xiaoyv.bangumi.features.index.detail.addIndexDetailScreen
import com.xiaoyv.bangumi.features.index.detail.navigateIndexDetail
import com.xiaoyv.bangumi.features.main.addMainScreen
import com.xiaoyv.bangumi.features.main.navigateMain
import com.xiaoyv.bangumi.features.main.tab.home.addCalendarScreen
import com.xiaoyv.bangumi.features.main.tab.home.addHomeScreen
import com.xiaoyv.bangumi.features.main.tab.home.navigateCalendar
import com.xiaoyv.bangumi.features.main.tab.home.navigateHome
import com.xiaoyv.bangumi.features.main.tab.newest.addNewestScreen
import com.xiaoyv.bangumi.features.main.tab.newest.navigateNewest
import com.xiaoyv.bangumi.features.main.tab.profile.addProfileScreen
import com.xiaoyv.bangumi.features.main.tab.profile.navigateProfile
import com.xiaoyv.bangumi.features.main.tab.timeline.addTimelineScreen
import com.xiaoyv.bangumi.features.main.tab.timeline.navigateTimeline
import com.xiaoyv.bangumi.features.main.tab.topic.addTopicScreen
import com.xiaoyv.bangumi.features.main.tab.topic.navigateTopic
import com.xiaoyv.bangumi.features.main.tab.tracking.addTrackingScreen
import com.xiaoyv.bangumi.features.main.tab.tracking.navigateTracking
import com.xiaoyv.bangumi.features.message.addMessageMainScreen
import com.xiaoyv.bangumi.features.message.chat.addMessageChatScreen
import com.xiaoyv.bangumi.features.message.chat.navigateMessageChat
import com.xiaoyv.bangumi.features.message.navigateMessageMain
import com.xiaoyv.bangumi.features.mikan.detail.addMikanDetailScreen
import com.xiaoyv.bangumi.features.mikan.detail.navigateMikanDetail
import com.xiaoyv.bangumi.features.mikan.studio.addMikanStudioScreen
import com.xiaoyv.bangumi.features.mikan.studio.navigateMikanStudio
import com.xiaoyv.bangumi.features.mono.browser.addMonoBrowserScreen
import com.xiaoyv.bangumi.features.mono.browser.navigateMonoBrowser
import com.xiaoyv.bangumi.features.mono.detail.addMonoDetailScreen
import com.xiaoyv.bangumi.features.mono.detail.navigateMonoDetail
import com.xiaoyv.bangumi.features.notification.addNotificationScreen
import com.xiaoyv.bangumi.features.notification.navigateNotification
import com.xiaoyv.bangumi.features.pixiv.login.addPixivLoginScreen
import com.xiaoyv.bangumi.features.pixiv.login.navigatePixivLogin
import com.xiaoyv.bangumi.features.pixiv.main.addPixivMainScreen
import com.xiaoyv.bangumi.features.pixiv.main.navigatePixivMain
import com.xiaoyv.bangumi.features.preivew.gallery.addPreviewTextScreen
import com.xiaoyv.bangumi.features.preivew.gallery.navigatePreviewText
import com.xiaoyv.bangumi.features.preivew.main.addPreviewMainScreen
import com.xiaoyv.bangumi.features.preivew.main.navigatePreviewMain
import com.xiaoyv.bangumi.features.search.input.addSearchInputScreen
import com.xiaoyv.bangumi.features.search.input.navigateSearchInput
import com.xiaoyv.bangumi.features.search.result.addSearchResultScreen
import com.xiaoyv.bangumi.features.search.result.navigateSearchResult
import com.xiaoyv.bangumi.features.settings.account.addSettingsAccountScreen
import com.xiaoyv.bangumi.features.settings.account.navigateSettingsAccount
import com.xiaoyv.bangumi.features.settings.bar.addSettingsBarScreen
import com.xiaoyv.bangumi.features.settings.bar.navigateSettingsBar
import com.xiaoyv.bangumi.features.settings.block.addSettingsBlockScreen
import com.xiaoyv.bangumi.features.settings.block.navigateSettingsBlock
import com.xiaoyv.bangumi.features.settings.live2d.addSettingsLive2dScreen
import com.xiaoyv.bangumi.features.settings.live2d.navigateSettingsLive2d
import com.xiaoyv.bangumi.features.settings.main.addSettingsMainScreen
import com.xiaoyv.bangumi.features.settings.main.navigateSettingsMain
import com.xiaoyv.bangumi.features.settings.network.addSettingsNetworkScreen
import com.xiaoyv.bangumi.features.settings.network.navigateSettingsNetwork
import com.xiaoyv.bangumi.features.settings.privacy.addSettingsPrivacyScreen
import com.xiaoyv.bangumi.features.settings.privacy.navigateSettingsPrivacy
import com.xiaoyv.bangumi.features.settings.translate.addSettingsTranslateScreen
import com.xiaoyv.bangumi.features.settings.translate.navigateSettingsTranslate
import com.xiaoyv.bangumi.features.settings.ui.addSettingsUiScreen
import com.xiaoyv.bangumi.features.settings.ui.navigateSettingsUi
import com.xiaoyv.bangumi.features.sign.sign_in.addSignInScreen
import com.xiaoyv.bangumi.features.sign.sign_in.navigateSignIn
import com.xiaoyv.bangumi.features.splash.addSplashScreen
import com.xiaoyv.bangumi.features.splash.navigateSplash
import com.xiaoyv.bangumi.features.subject.browser.addSubjectBrowserScreen
import com.xiaoyv.bangumi.features.subject.browser.navigateSubjectBrowser
import com.xiaoyv.bangumi.features.subject.detail.addSubjectDetailScreen
import com.xiaoyv.bangumi.features.subject.detail.navigateSubjectDetail
import com.xiaoyv.bangumi.features.tag.detail.addTagDetailScreen
import com.xiaoyv.bangumi.features.tag.detail.navigateTagDetail
import com.xiaoyv.bangumi.features.timeline.detail.addTimelineDetailScreen
import com.xiaoyv.bangumi.features.timeline.detail.navigateTimelineDetail
import com.xiaoyv.bangumi.features.topic.detail.navigateTopicDetail
import com.xiaoyv.bangumi.features.user.addUserScreen
import com.xiaoyv.bangumi.features.user.navigateUser
import com.xiaoyv.bangumi.features.web.addWebScreen
import com.xiaoyv.bangumi.features.web.navigateWeb
import com.xiaoyv.bangumi.shared.core.utils.debounce
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.ScreenNavHost
import org.koin.mp.KoinPlatform


@Composable
fun BgmScreenNavGraph(
    navController: NavHostController,
    startDestination: Screen,
) {
    val onNavigateUp = remember(navController) { { debounce("navigateUp") { navController.navigateUp() } } }

    ScreenNavHost(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        navController = navController,
        startDestination = startDestination
    ) {
        addSplashScreen(navController::navigateToScreen)
        addAlmanacScreen(onNavigateUp, navController::navigateToScreen)
        addMainScreen(navController::navigateToScreen)
        addSignInScreen(onNavigateUp, navController::navigateToScreen)
        addHomeScreen(onNavigateUp, navController::navigateToScreen)
        addTimelineScreen(onNavigateUp, navController::navigateToScreen)
        addTopicScreen(onNavigateUp, navController::navigateToScreen)
        addProfileScreen(onNavigateUp, navController::navigateToScreen)
        addTrackingScreen(onNavigateUp, navController::navigateToScreen)
        addMessageMainScreen(onNavigateUp, navController::navigateToScreen)
        addMessageChatScreen(onNavigateUp, navController::navigateToScreen)
        addNotificationScreen(onNavigateUp, navController::navigateToScreen)
        addSettingsMainScreen(onNavigateUp, navController::navigateToScreen)
        addSettingsUiScreen(onNavigateUp, navController::navigateToScreen)
        addSettingsBarScreen(onNavigateUp, navController::navigateToScreen)
        addSettingsBlockScreen(onNavigateUp, navController::navigateToScreen)
        addSettingsLive2dScreen(onNavigateUp, navController::navigateToScreen)
        addSettingsAccountScreen(onNavigateUp, navController::navigateToScreen)
        addSettingsNetworkScreen(onNavigateUp, navController::navigateToScreen)
        addSettingsPrivacyScreen(onNavigateUp, navController::navigateToScreen)
        addSettingsTranslateScreen(onNavigateUp, navController::navigateToScreen)
        addMikanDetailScreen(onNavigateUp, navController::navigateToScreen)
        addMikanStudioScreen(onNavigateUp, navController::navigateToScreen)
        addSearchInputScreen(onNavigateUp, navController::navigateToScreen)
        addSearchResultScreen(onNavigateUp, navController::navigateToScreen)
        addSubjectDetailScreen(onNavigateUp, navController::navigateToScreen)
        addReceiveScreen(onNavigateUp, navController::navigateToScreen)
        addMonoDetailScreen(onNavigateUp, navController::navigateToScreen)
        addMonoBrowserScreen(onNavigateUp, navController::navigateToScreen)
        addGalleryScreen(onNavigateUp, navController::navigateToScreen)
        addWebScreen(onNavigateUp, navController::navigateToScreen)
        addArticleScreen(onNavigateUp, navController::navigateToScreen)
        addNewestScreen(onNavigateUp, navController::navigateToScreen)
        addDollarsScreen(onNavigateUp, navController::navigateToScreen)
        addBlogPageScreen(onNavigateUp, navController::navigateToScreen)
        addIndexDetailScreen(onNavigateUp, navController::navigateToScreen)
        addGroupsDetailScreen(onNavigateUp, navController::navigateToScreen)
        addPreviewTextScreen(onNavigateUp, navController::navigateToScreen)
        addUserScreen(onNavigateUp, navController::navigateToScreen)
        addCalendarScreen(onNavigateUp, navController::navigateToScreen)
        addTimelineDetailScreen(onNavigateUp, navController::navigateToScreen)
        addTagDetailScreen(onNavigateUp, navController::navigateToScreen)
        addSubjectBrowserScreen(onNavigateUp, navController::navigateToScreen)
        addPreviewMainScreen(onNavigateUp, navController::navigateToScreen)
        addPixivMainScreen(onNavigateUp, navController::navigateToScreen)
        addPixivLoginScreen(onNavigateUp, navController::navigateToScreen)
        addGardenScreen(onNavigateUp, navController::navigateToScreen)
    }
}


private inline fun NavHostController.checkLogin(block: () -> Unit) {
    val manager = KoinPlatform.getKoin().get<UserManager>()
    if (manager.isLogin) block() else navigateSignIn(Screen.SignIn)
}

internal fun NavHostController.navigateToScreen(screen: Screen) {
    when (screen) {
        is Screen.Empty -> Unit
        is Screen.Web -> navigateWeb(screen)
        is Screen.SubjectDetail -> navigateSubjectDetail(screen)
        is Screen.MonoDetail -> navigateMonoDetail(screen)
        is Screen.MonoBrowser -> navigateMonoBrowser(screen)
        is Screen.Main -> navigateMain(screen)
        is Screen.Splash -> navigateSplash(screen)
        is Screen.SignIn -> navigateSignIn(screen)
        is Screen.Home -> navigateHome(screen)
        is Screen.Profile -> navigateProfile(screen)
        is Screen.Timeline -> navigateTimeline(screen)
        is Screen.Topic -> navigateTopic(screen)
        is Screen.TopicDetail -> navigateTopicDetail(screen)
        is Screen.DetectImage -> navigateReceive(screen)
        is Screen.Tracking -> checkLogin { navigateTracking(screen) }
        is Screen.SettingsMain -> navigateSettingsMain(screen)
        is Screen.SettingsAccount -> checkLogin { navigateSettingsAccount(screen) }
        is Screen.SettingsBar -> navigateSettingsBar(screen)
        is Screen.SettingsBlock -> checkLogin { navigateSettingsBlock(screen) }
        is Screen.SettingsLive2d -> navigateSettingsLive2d(screen)
        is Screen.SettingsNetwork -> navigateSettingsNetwork(screen)
        is Screen.SettingsPrivacy -> checkLogin { navigateSettingsPrivacy(screen) }
        is Screen.SettingsTranslate -> navigateSettingsTranslate(screen)
        is Screen.SettingsUi -> navigateSettingsUi(screen)
        is Screen.MessageMain -> checkLogin { navigateMessageMain(screen) }
        is Screen.MessageChat -> checkLogin { navigateMessageChat(screen) }
        is Screen.Notification -> checkLogin { navigateNotification(screen) }
        is Screen.MikanResources -> navigateMikanDetail(screen)
        is Screen.MikanStudio -> navigateMikanStudio(screen)
        is Screen.Gallery -> navigateGallery(screen)
        is Screen.SearchInput -> navigateSearchInput(screen)
        is Screen.SearchResult -> navigateSearchResult(screen)
        is Screen.Article -> navigateArticle(screen)
        is Screen.Newest -> navigateNewest(screen)
        is Screen.Dollars -> checkLogin { navigateDollars(screen) }
        is Screen.BlogList -> navigateBlogPage(screen)
        is Screen.IndexDetail -> navigateIndexDetail(screen)
        is Screen.GroupDetail -> navigateGroupsDetail(screen)
        is Screen.PreviewText -> navigatePreviewText(screen)
        is Screen.UserDetail -> navigateUser(screen)
        is Screen.TimelineDetail -> navigateTimelineDetail(screen)
        is Screen.Calendar -> navigateCalendar(screen)
        is Screen.TagDetail -> navigateTagDetail(screen)
        is Screen.SubjectBrowser -> navigateSubjectBrowser(screen)
        is Screen.PreviewMain -> navigatePreviewMain(screen)
        is Screen.PixivMain -> navigatePixivMain(screen)
        is Screen.PixivLogin -> navigatePixivLogin(screen)
        is Screen.Almanac -> navigateAlmanac(screen)
        is Screen.Garden -> navigateGarden(screen)
    }
}
