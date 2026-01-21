package com.xiaoyv.bangumi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xiaoyv.bangumi.features.almanac.almanacModule
import com.xiaoyv.bangumi.features.article.articleModule
import com.xiaoyv.bangumi.features.detect.receiveImageModule
import com.xiaoyv.bangumi.features.dollars.dollarsModule
import com.xiaoyv.bangumi.features.gallery.galleryModule
import com.xiaoyv.bangumi.features.garden.gardenModule
import com.xiaoyv.bangumi.features.groups.detail.groupsDetailModule
import com.xiaoyv.bangumi.features.index.detail.indexDetailModule
import com.xiaoyv.bangumi.features.main.mainModule
import com.xiaoyv.bangumi.features.main.tab.home.calendarModule
import com.xiaoyv.bangumi.features.main.tab.home.homeModule
import com.xiaoyv.bangumi.features.main.tab.newest.newestModule
import com.xiaoyv.bangumi.features.main.tab.profile.profileModule
import com.xiaoyv.bangumi.features.main.tab.timeline.timelineModule
import com.xiaoyv.bangumi.features.main.tab.topic.topicModule
import com.xiaoyv.bangumi.features.main.tab.tracking.trackingModule
import com.xiaoyv.bangumi.features.message.chat.messageChatModule
import com.xiaoyv.bangumi.features.message.messageMainModule
import com.xiaoyv.bangumi.features.mikan.detail.mikanDetailModule
import com.xiaoyv.bangumi.features.mikan.studio.mikanStudioModule
import com.xiaoyv.bangumi.features.mono.browser.monoBrowserModule
import com.xiaoyv.bangumi.features.mono.detail.monoDetailModule
import com.xiaoyv.bangumi.features.notification.notificationModule
import com.xiaoyv.bangumi.features.pixiv.login.pixivLoginModule
import com.xiaoyv.bangumi.features.pixiv.main.pixivMainModule
import com.xiaoyv.bangumi.features.preivew.gallery.previewTextModule
import com.xiaoyv.bangumi.features.preivew.main.previewMainModule
import com.xiaoyv.bangumi.features.search.input.searchInputModule
import com.xiaoyv.bangumi.features.search.result.searchResultModule
import com.xiaoyv.bangumi.features.settings.account.settingsAccountModule
import com.xiaoyv.bangumi.features.settings.bar.settingsBarModule
import com.xiaoyv.bangumi.features.settings.block.settingsBlockModule
import com.xiaoyv.bangumi.features.settings.live2d.settingsLive2dModule
import com.xiaoyv.bangumi.features.settings.main.settingsMainModule
import com.xiaoyv.bangumi.features.settings.network.settingsNetworkModule
import com.xiaoyv.bangumi.features.settings.privacy.settingsPrivacyModule
import com.xiaoyv.bangumi.features.settings.translate.settingsTranslateModule
import com.xiaoyv.bangumi.features.settings.ui.settingsUiModule
import com.xiaoyv.bangumi.features.sign.sign_in.signInModule
import com.xiaoyv.bangumi.features.splash.splashModule
import com.xiaoyv.bangumi.features.subject.browser.subjectBrowserModule
import com.xiaoyv.bangumi.features.subject.detail.subjectDetailModule
import com.xiaoyv.bangumi.features.tag.detail.tagDetailModule
import com.xiaoyv.bangumi.features.timeline.detail.timelineDetailModule
import com.xiaoyv.bangumi.features.topic.detail.topicDetailModule
import com.xiaoyv.bangumi.features.user.userModule
import com.xiaoyv.bangumi.features.web.webModule
import com.xiaoyv.bangumi.shared.ui.component.navigation.Navigator
import com.xiaoyv.bangumi.shared.ui.component.navigation.ScreenNavHost
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module

@OptIn(KoinExperimentalAPI::class)
internal val navigationModule = module {
    single { Navigator() }

    includes(
        splashModule,
        mainModule,
        signInModule,
        timelineModule,
        timelineDetailModule,
        topicModule,
        trackingModule,
        profileModule,
        homeModule,
        almanacModule,
        settingsMainModule,
        settingsAccountModule,
        settingsBarModule,
        settingsBlockModule,
        settingsLive2dModule,
        settingsNetworkModule,
        settingsPrivacyModule,
        settingsTranslateModule,
        settingsUiModule,
        notificationModule,
        messageMainModule,
        messageChatModule,
        newestModule,
        dollarsModule,
        userModule,
        groupsDetailModule,
        indexDetailModule,
        tagDetailModule,
        pixivMainModule,
        pixivLoginModule,
        topicDetailModule,
        articleModule,
        receiveImageModule,
        searchInputModule,
        searchResultModule,
        mikanDetailModule,
        mikanStudioModule,
        galleryModule,
        previewTextModule,
        gardenModule,
        subjectDetailModule,
        subjectBrowserModule,
        previewMainModule,
        monoDetailModule,
        monoBrowserModule,
        webModule,
        calendarModule
    )
}

@OptIn(KoinExperimentalAPI::class)
@Composable
fun BgmScreenNavGraph(navigator: Navigator) {
    ScreenNavHost(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        navigator = navigator
    )
}


