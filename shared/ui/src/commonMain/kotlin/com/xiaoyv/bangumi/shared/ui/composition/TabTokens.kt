@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.ui.composition

import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.Slideshow
import androidx.compose.material.icons.rounded.Sms
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material.icons.rounded.ViewTimeline
import androidx.compose.material.icons.rounded.VisibilityOff
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.calendar_friday
import com.xiaoyv.bangumi.core_resource.resources.calendar_monday
import com.xiaoyv.bangumi.core_resource.resources.calendar_saturday
import com.xiaoyv.bangumi.core_resource.resources.calendar_sunday
import com.xiaoyv.bangumi.core_resource.resources.calendar_thursday
import com.xiaoyv.bangumi.core_resource.resources.calendar_tuesday
import com.xiaoyv.bangumi.core_resource.resources.calendar_wednesday
import com.xiaoyv.bangumi.core_resource.resources.global_all
import com.xiaoyv.bangumi.core_resource.resources.global_anime
import com.xiaoyv.bangumi.core_resource.resources.global_blog
import com.xiaoyv.bangumi.core_resource.resources.global_book
import com.xiaoyv.bangumi.core_resource.resources.global_collection
import com.xiaoyv.bangumi.core_resource.resources.global_dynamic
import com.xiaoyv.bangumi.core_resource.resources.global_friend
import com.xiaoyv.bangumi.core_resource.resources.global_game
import com.xiaoyv.bangumi.core_resource.resources.global_grid
import com.xiaoyv.bangumi.core_resource.resources.global_group
import com.xiaoyv.bangumi.core_resource.resources.global_hide
import com.xiaoyv.bangumi.core_resource.resources.global_home
import com.xiaoyv.bangumi.core_resource.resources.global_hot
import com.xiaoyv.bangumi.core_resource.resources.global_hot_group
import com.xiaoyv.bangumi.core_resource.resources.global_index
import com.xiaoyv.bangumi.core_resource.resources.global_list
import com.xiaoyv.bangumi.core_resource.resources.global_mono
import com.xiaoyv.bangumi.core_resource.resources.global_music
import com.xiaoyv.bangumi.core_resource.resources.global_newest
import com.xiaoyv.bangumi.core_resource.resources.global_off
import com.xiaoyv.bangumi.core_resource.resources.global_on
import com.xiaoyv.bangumi.core_resource.resources.global_person
import com.xiaoyv.bangumi.core_resource.resources.global_progress
import com.xiaoyv.bangumi.core_resource.resources.global_real
import com.xiaoyv.bangumi.core_resource.resources.global_show
import com.xiaoyv.bangumi.core_resource.resources.global_sort_bookmark
import com.xiaoyv.bangumi.core_resource.resources.global_sort_date
import com.xiaoyv.bangumi.core_resource.resources.global_sort_rank
import com.xiaoyv.bangumi.core_resource.resources.global_sort_rating
import com.xiaoyv.bangumi.core_resource.resources.global_sort_title
import com.xiaoyv.bangumi.core_resource.resources.global_sort_trends
import com.xiaoyv.bangumi.core_resource.resources.global_sort_update
import com.xiaoyv.bangumi.core_resource.resources.global_spit_out
import com.xiaoyv.bangumi.core_resource.resources.global_wiki
import com.xiaoyv.bangumi.core_resource.resources.ic_bottom_rank
import com.xiaoyv.bangumi.core_resource.resources.ic_calendar
import com.xiaoyv.bangumi.core_resource.resources.ic_chat
import com.xiaoyv.bangumi.core_resource.resources.ic_dollars
import com.xiaoyv.bangumi.core_resource.resources.ic_globe
import com.xiaoyv.bangumi.core_resource.resources.ic_image_search
import com.xiaoyv.bangumi.core_resource.resources.ic_manage_search
import com.xiaoyv.bangumi.core_resource.resources.ic_new
import com.xiaoyv.bangumi.core_resource.resources.ic_person_search
import com.xiaoyv.bangumi.core_resource.resources.ic_process
import com.xiaoyv.bangumi.core_resource.resources.ic_show
import com.xiaoyv.bangumi.core_resource.resources.ic_tag
import com.xiaoyv.bangumi.core_resource.resources.ic_timeline
import com.xiaoyv.bangumi.core_resource.resources.search_match_e
import com.xiaoyv.bangumi.core_resource.resources.search_match_m
import com.xiaoyv.bangumi.core_resource.resources.search_sort_date
import com.xiaoyv.bangumi.core_resource.resources.search_sort_date_create
import com.xiaoyv.bangumi.core_resource.resources.search_sort_date_update
import com.xiaoyv.bangumi.core_resource.resources.search_sort_relation
import com.xiaoyv.bangumi.core_resource.resources.settings_indication_fade
import com.xiaoyv.bangumi.core_resource.resources.settings_indication_none
import com.xiaoyv.bangumi.core_resource.resources.settings_indication_ripped
import com.xiaoyv.bangumi.core_resource.resources.settings_navigation_animation_fade
import com.xiaoyv.bangumi.core_resource.resources.settings_navigation_animation_none
import com.xiaoyv.bangumi.core_resource.resources.settings_navigation_animation_slide
import com.xiaoyv.bangumi.core_resource.resources.settings_theme_dark
import com.xiaoyv.bangumi.core_resource.resources.settings_theme_light
import com.xiaoyv.bangumi.core_resource.resources.settings_theme_system
import com.xiaoyv.bangumi.core_resource.resources.timeline_blog
import com.xiaoyv.bangumi.core_resource.resources.timeline_daily
import com.xiaoyv.bangumi.core_resource.resources.timeline_index
import com.xiaoyv.bangumi.core_resource.resources.timeline_mono
import com.xiaoyv.bangumi.core_resource.resources.timeline_progress
import com.xiaoyv.bangumi.core_resource.resources.timeline_status
import com.xiaoyv.bangumi.core_resource.resources.timeline_subject
import com.xiaoyv.bangumi.core_resource.resources.timeline_wiki
import com.xiaoyv.bangumi.core_resource.resources.timeline_window
import com.xiaoyv.bangumi.core_resource.resources.type_feature_discover
import com.xiaoyv.bangumi.core_resource.resources.type_feature_hidden
import com.xiaoyv.bangumi.core_resource.resources.type_feature_pm
import com.xiaoyv.bangumi.core_resource.resources.type_feature_process
import com.xiaoyv.bangumi.core_resource.resources.type_feature_profile
import com.xiaoyv.bangumi.core_resource.resources.type_feature_rakuen
import com.xiaoyv.bangumi.core_resource.resources.type_feature_rank
import com.xiaoyv.bangumi.core_resource.resources.type_feature_schedule
import com.xiaoyv.bangumi.core_resource.resources.type_feature_subject_browser
import com.xiaoyv.bangumi.core_resource.resources.type_feature_tag
import com.xiaoyv.bangumi.core_resource.resources.type_feature_timeline
import com.xiaoyv.bangumi.core_resource.resources.type_group_created
import com.xiaoyv.bangumi.core_resource.resources.type_group_members
import com.xiaoyv.bangumi.core_resource.resources.type_group_topics
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.CollectionWebSortType
import com.xiaoyv.bangumi.shared.core.types.FeatureType
import com.xiaoyv.bangumi.shared.core.types.HomeTab
import com.xiaoyv.bangumi.shared.core.types.IndexHomepageType
import com.xiaoyv.bangumi.shared.core.types.LayoutType
import com.xiaoyv.bangumi.shared.core.types.MagnetGardenSort
import com.xiaoyv.bangumi.shared.core.types.MagnetGardenTeam
import com.xiaoyv.bangumi.shared.core.types.MagnetGardenType
import com.xiaoyv.bangumi.shared.core.types.MonoCastType
import com.xiaoyv.bangumi.shared.core.types.SubjectSortBrowserType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.types.SubjectWebPath
import com.xiaoyv.bangumi.shared.core.types.TimelineCat
import com.xiaoyv.bangumi.shared.core.types.TimelineTab
import com.xiaoyv.bangumi.shared.core.types.settings.SettingIndication
import com.xiaoyv.bangumi.shared.core.types.settings.SettingNavigationAnimation
import com.xiaoyv.bangumi.shared.core.types.settings.SettingTheme
import com.xiaoyv.bangumi.shared.core.types.settings.SettingUpdateChannel
import com.xiaoyv.bangumi.shared.data.model.emnu.GroupSortType
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.SubjectBrowserBody
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeDrawableTab
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeVectorTab
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList
import org.jetbrains.compose.resources.DrawableResource

object TabTokens {
    const val TAB_GROUP_HOMEPAGE = "TAB_GROUP_HOT"

    val ScreenBrowser = Screen.SubjectBrowser(
        body = SubjectBrowserBody(
            subjectType = SubjectType.ANIME,
            sort = SubjectSortBrowserType.TRENDS,
        )
    )

    val ScreenRank = Screen.SubjectBrowser(
        body = SubjectBrowserBody(
            subjectType = SubjectType.ANIME,
            sort = SubjectSortBrowserType.RANK,
            hideSortFilter = true
        )
    )

    val filterNsfw = persistentListOf(
        ComposeTextTab(true, Res.string.global_on),
        ComposeTextTab(false, Res.string.global_off),
    )

    val searchTopicSort = persistentListOf(
        ComposeTextTab("", Res.string.search_sort_relation),
        ComposeTextTab("time_date", Res.string.search_sort_date),
    )

    val searchIndexSort = persistentListOf(
        ComposeTextTab("", Res.string.search_sort_relation),
        ComposeTextTab("updated_at", Res.string.search_sort_date_update),
        ComposeTextTab("created_at", Res.string.search_sort_date_create),
    )

    val searchSummaryTabs = persistentListOf(
        ComposeTextTab(true, Res.string.global_show),
        ComposeTextTab(false, Res.string.global_hide),
    )

    val searchMatchTabs = persistentListOf(
        ComposeTextTab(false, Res.string.search_match_m),
        ComposeTextTab(true, Res.string.search_match_e),
    )

    val layoutTabs = persistentListOf(
        ComposeTextTab(LayoutType.LIST, Res.string.global_list),
        ComposeTextTab(LayoutType.GRID, Res.string.global_grid),
    )

    val subjectTypeAllTabs = persistentListOf(
        ComposeTextTab(SubjectType.UNKNOWN, Res.string.global_all),
        ComposeTextTab(SubjectType.ANIME, Res.string.global_anime),
        ComposeTextTab(SubjectType.BOOK, Res.string.global_book),
        ComposeTextTab(SubjectType.MUSIC, Res.string.global_music),
        ComposeTextTab(SubjectType.GAME, Res.string.global_game),
        ComposeTextTab(SubjectType.REAL, Res.string.global_real),
    )

    val subjectTypeTabs = persistentListOf(
        ComposeTextTab(SubjectType.ANIME, Res.string.global_anime),
        ComposeTextTab(SubjectType.BOOK, Res.string.global_book),
        ComposeTextTab(SubjectType.MUSIC, Res.string.global_music),
        ComposeTextTab(SubjectType.GAME, Res.string.global_game),
        ComposeTextTab(SubjectType.REAL, Res.string.global_real),
    )

    val subjectBrowserSortTabs = persistentListOf(
        ComposeTextTab(SubjectSortBrowserType.TITLE, Res.string.global_sort_title),
        ComposeTextTab(SubjectSortBrowserType.RANK, Res.string.global_sort_rank),
        ComposeTextTab(SubjectSortBrowserType.COLLECTS, Res.string.global_sort_bookmark),
        ComposeTextTab(SubjectSortBrowserType.TRENDS, Res.string.global_sort_trends),
        ComposeTextTab(SubjectSortBrowserType.DATE, Res.string.global_sort_date),
    )

    val mainTabIndex = persistentListOf(
        ComposeTextTab(0, labelText = "Tab-1"),
        ComposeTextTab(1, labelText = "Tab-2"),
        ComposeTextTab(2, labelText = "Tab-3"),
        ComposeTextTab(3, labelText = "Tab-4"),
        ComposeTextTab(4, labelText = "Tab-5"),
    )


    val mainTabFeatureItems = persistentMapOf(
        Screen.Empty to ComposeVectorTab(
            type = FeatureType.TYPE_UNSET,
            label = Res.string.type_feature_hidden,
            icon = BgmIcons.VisibilityOff
        ),
        Screen.Home to ComposeVectorTab(
            type = FeatureType.TYPE_HOME,
            label = Res.string.type_feature_discover,
            icon = BgmIcons.Home
        ),
        Screen.Timeline to ComposeVectorTab(
            type = FeatureType.TYPE_TIMELINE,
            label = Res.string.type_feature_timeline,
            icon = BgmIcons.ViewTimeline
        ),
        Screen.Tracking to ComposeVectorTab(
            type = FeatureType.TYPE_TRACKING,
            label = Res.string.type_feature_process,
            icon = BgmIcons.LiveTv
        ),
        Screen.TagDetail() to ComposeVectorTab(
            type = FeatureType.TYPE_TAG,
            label = Res.string.type_feature_tag,
            icon = BgmIcons.Tag
        ),
        ScreenRank to ComposeVectorTab(
            type = FeatureType.TYPE_RANK,
            label = Res.string.type_feature_rank,
            icon = BgmIcons.BarChart
        ),
        ScreenBrowser to ComposeVectorTab(
            type = FeatureType.TYPE_SUBJECT_BROWSER,
            label = Res.string.type_feature_subject_browser,
            icon = BgmIcons.Explore
        ),
        Screen.Topic to ComposeVectorTab(
            type = FeatureType.TYPE_RAKUEN,
            label = Res.string.type_feature_rakuen,
            icon = BgmIcons.Forum
        ),
        Screen.Profile to ComposeVectorTab(
            type = FeatureType.TYPE_PROFILE,
            label = Res.string.type_feature_profile,
            icon = BgmIcons.AccountCircle
        ),
        Screen.Calendar(true) to ComposeVectorTab(
            type = FeatureType.TYPE_SCHEDULE,
            label = Res.string.type_feature_schedule,
            icon = BgmIcons.Slideshow
        ),
        Screen.MessageMain to ComposeVectorTab(
            type = FeatureType.TYPE_SMS,
            label = Res.string.type_feature_pm,
            icon = BgmIcons.Sms
        ),
    )

    val mainTabFeatures: PersistentList<ComposeTextTab<String>> = mainTabFeatureItems
        .map { ComposeTextTab(it.value.type, it.value.label) }
        .toPersistentList()

    val mainHomeTabs = persistentListOf(
        ComposeTextTab(HomeTab.HOME, Res.string.global_home),
        ComposeTextTab(HomeTab.MONO, Res.string.global_person),
        ComposeTextTab(HomeTab.BLOG, Res.string.global_blog),
        ComposeTextTab(HomeTab.GROUP, Res.string.global_group),
        ComposeTextTab(HomeTab.INDEX, Res.string.global_index),
    )

    val mainHomeActions = persistentListOf(
//        createFeature(FeatureType.TYPE_MAGI, Res.drawable.ic_magi),
        createFeature(FeatureType.TYPE_NEWEST, Res.drawable.ic_new),
//        createFeature(FeatureType.TYPE_SYNCER, Res.drawable.ic_sync_cloud),
        createFeature(FeatureType.TYPE_RANK, Res.drawable.ic_bottom_rank),
        createFeature(FeatureType.TYPE_TRACKING, Res.drawable.ic_process),
        createFeature(FeatureType.TYPE_ALMANAC, Res.drawable.ic_calendar),
        createFeature(FeatureType.TYPE_SCHEDULE, Res.drawable.ic_show),
        createFeature(FeatureType.TYPE_SUBJECT_BROWSER, Res.drawable.ic_globe),
        createFeature(FeatureType.TYPE_TAG, Res.drawable.ic_tag),
        createFeature(FeatureType.TYPE_DETECT_ANIME, Res.drawable.ic_image_search),
        createFeature(FeatureType.TYPE_DETECT_CHARACTER, Res.drawable.ic_person_search),
        createFeature(FeatureType.TYPE_MAGNET, Res.drawable.ic_manage_search),
        createFeature(FeatureType.TYPE_DOLLARS, Res.drawable.ic_dollars),
        createFeature(FeatureType.TYPE_TIMELINE, Res.drawable.ic_timeline),
        createFeature(FeatureType.TYPE_RAKUEN, Res.drawable.ic_chat),
//        createFeature(FeatureType.TYPE_PIXIV, Res.drawable.ic_pixiv),
    )

    val mainHomeBlogFilters = persistentListOf(
        ComposeTextTab("", Res.string.global_all),
        ComposeTextTab(SubjectWebPath.BOOK, Res.string.global_book),
        ComposeTextTab(SubjectWebPath.ANIME, Res.string.global_anime),
        ComposeTextTab(SubjectWebPath.MUSIC, Res.string.global_music),
        ComposeTextTab(SubjectWebPath.GAME, Res.string.global_game),
        ComposeTextTab(SubjectWebPath.REAL, Res.string.global_real),
    )
    val mainHomeGroupFilters = persistentListOf(
        ComposeTextTab(TAB_GROUP_HOMEPAGE, Res.string.global_hot_group),
        ComposeTextTab(GroupSortType.TOPICS, Res.string.type_group_topics),
        ComposeTextTab(GroupSortType.MEMBERS, Res.string.type_group_members),
        ComposeTextTab(GroupSortType.CREATED, Res.string.type_group_created),
    )
    val mainHomeIndexFilters = persistentListOf(
        ComposeTextTab(IndexHomepageType.HOT, Res.string.global_hot),
        ComposeTextTab(IndexHomepageType.NEWEST, Res.string.global_newest),
    )

    val subjectTypeFilters = persistentListOf(
        ComposeTextTab(SubjectType.ANIME, Res.string.global_anime),
        ComposeTextTab(SubjectType.BOOK, Res.string.global_book),
        ComposeTextTab(SubjectType.MUSIC, Res.string.global_music),
        ComposeTextTab(SubjectType.GAME, Res.string.global_game),
        ComposeTextTab(SubjectType.REAL, Res.string.global_real),
    )

    val collectionTypeFilters = persistentListOf(
        ComposeTextTab(CollectionType.WISH),
        ComposeTextTab(CollectionType.DONE),
        ComposeTextTab(CollectionType.DOING),
        ComposeTextTab(CollectionType.ASIDE),
        ComposeTextTab(CollectionType.DROP),
    )

    val collectionSortFilters = persistentListOf(
        ComposeTextTab(CollectionWebSortType.RATE, Res.string.global_sort_rating),
        ComposeTextTab(CollectionWebSortType.COLLECT_TIME, Res.string.global_sort_update),
        ComposeTextTab(CollectionWebSortType.DATE, Res.string.global_sort_date),
        ComposeTextTab(CollectionWebSortType.TITLE, Res.string.global_sort_title),
    )

    val timelineWebTabs = persistentListOf(
        ComposeTextTab(TimelineTab.DYNAMIC, Res.string.global_dynamic),
        ComposeTextTab(TimelineTab.SPIT_OUT, Res.string.global_spit_out),
        ComposeTextTab(TimelineTab.BOOKMARK, Res.string.global_collection),
        ComposeTextTab(TimelineTab.PROGRESS, Res.string.global_progress),
        ComposeTextTab(TimelineTab.BLOG, Res.string.global_blog),
        ComposeTextTab(TimelineTab.MONO, Res.string.global_mono),
        ComposeTextTab(TimelineTab.FRIEND, Res.string.global_friend),
        ComposeTextTab(TimelineTab.GROUP, Res.string.global_group),
        ComposeTextTab(TimelineTab.WIKI, Res.string.global_wiki),
        ComposeTextTab(TimelineTab.INDEX, Res.string.global_index),
    )

    val timelineCatTabs = persistentListOf(
        ComposeTextTab(TimelineCat.UNKNOWN, Res.string.global_all),
//        ComposeTextTab(TimelineCat.STATUS, Res.string.timeline_status),
//        ComposeTextTab(TimelineCat.SUBJECT, Res.string.timeline_subject),
//        ComposeTextTab(TimelineCat.PROGRESS, Res.string.timeline_progress),
//        ComposeTextTab(TimelineCat.BLOG, Res.string.timeline_blog),
//        ComposeTextTab(TimelineCat.DAILY, Res.string.timeline_daily),
//        ComposeTextTab(TimelineCat.MONO, Res.string.timeline_mono),
//        ComposeTextTab(TimelineCat.INDEX, Res.string.timeline_index),
//        ComposeTextTab(TimelineCat.WIKI, Res.string.timeline_wiki),
//        ComposeTextTab(TimelineCat.WINDOW, Res.string.timeline_window),
    )

    val calendarWeekDays = persistentListOf(
        ComposeTextTab(1, Res.string.calendar_monday),
        ComposeTextTab(2, Res.string.calendar_tuesday),
        ComposeTextTab(3, Res.string.calendar_wednesday),
        ComposeTextTab(4, Res.string.calendar_thursday),
        ComposeTextTab(5, Res.string.calendar_friday),
        ComposeTextTab(6, Res.string.calendar_saturday),
        ComposeTextTab(7, Res.string.calendar_sunday),
    )

    val subjectCharacterRoleFilters = persistentListOf(
        ComposeTextTab(MonoCastType.UNKNOWN, labelText = "全部"),
        ComposeTextTab(MonoCastType.MAIN, labelText = "主角"),
        ComposeTextTab(MonoCastType.CO_STAR, labelText = "配角"),
        ComposeTextTab(MonoCastType.GUEST, labelText = "客串"),
        ComposeTextTab(MonoCastType.OTHER, labelText = "闲角")
    )

    val settingBangumiHosts = persistentListOf(
        ComposeTextTab("https://bgm.tv/", labelText = "bgm.tv"),
        ComposeTextTab("https://bangumi.tv/", labelText = "bangumi.tv"),
        ComposeTextTab("https://chii.in/", labelText = "chii.in"),
    )

    val settingPixivImgHosts = persistentListOf(
        ComposeTextTab("https://xget.xiaoyv.com.cn/pximg/", labelText = "xget.xiaoyv.com.cn"),
        ComposeTextTab("https://imp.pximg.net/", labelText = "imp.pximg.net"),
        ComposeTextTab("https://source.pximg.net/", labelText = "source.pximg.net"),
        ComposeTextTab("https://i-f.pximg.net/", labelText = "i-f.pximg.net"),
        ComposeTextTab("https://i.pixiv.re/", labelText = "i.pixiv.re"),
        ComposeTextTab("https://i.pixiv.ln/", labelText = "i.pixiv.ln"),
    )

    val settingUpdateChannels = persistentListOf(
        ComposeTextTab(SettingUpdateChannel.RELEASE, labelText = "Release"),
        ComposeTextTab(SettingUpdateChannel.ACTION, labelText = "Action"),
    )

    val settingThemeItems = persistentListOf(
        ComposeTextTab(SettingTheme.SYSTEM, Res.string.settings_theme_system),
        ComposeTextTab(SettingTheme.LIGHT, Res.string.settings_theme_light),
        ComposeTextTab(SettingTheme.DARK, Res.string.settings_theme_dark),
    )

    val settingTimeoutItems = persistentListOf(
        ComposeTextTab(10_000L, labelText = "10s"),
        ComposeTextTab(15_000L, labelText = "15s"),
        ComposeTextTab(20_000L, labelText = "20s"),
        ComposeTextTab(25_000L, labelText = "25s"),
        ComposeTextTab(30_000L, labelText = "30s"),
    )

    val settingTimeMachineGridLimitItems = persistentListOf(
        ComposeTextTab(5, labelText = "5"),
        ComposeTextTab(10, labelText = "10"),
        ComposeTextTab(15, labelText = "15"),
        ComposeTextTab(20, labelText = "20"),
        ComposeTextTab(25, labelText = "25"),
        ComposeTextTab(30, labelText = "30"),
        ComposeTextTab(35, labelText = "35"),
        ComposeTextTab(40, labelText = "40"),
    )

    val settingNavigationAnimationItems = persistentListOf(
        ComposeTextTab(SettingNavigationAnimation.NONE, Res.string.settings_navigation_animation_none),
        ComposeTextTab(SettingNavigationAnimation.FADE, Res.string.settings_navigation_animation_fade),
        ComposeTextTab(SettingNavigationAnimation.SLIDE, Res.string.settings_navigation_animation_slide),
    )

    val settingIndicationItems = persistentListOf(
        ComposeTextTab(SettingIndication.NONE, Res.string.settings_indication_none),
        ComposeTextTab(SettingIndication.FADE, Res.string.settings_indication_fade),
        ComposeTextTab(SettingIndication.RIPPLE, Res.string.settings_indication_ripped),
    )

    val magnetGardenSorts by lazy {
        listOf(MagnetGardenSort.RELATED, MagnetGardenSort.DATE_ASC, MagnetGardenSort.DATE_DESC)
            .map { ComposeTextTab(it, labelText = MagnetGardenSort.string(it)) }
            .toPersistentList()
    }

    val magnetGardenTypes by lazy {
        listOf(
            MagnetGardenType.TYPE_ALL,
            MagnetGardenType.TYPE_ANIME,
            MagnetGardenType.TYPE_SEASONAL,
            MagnetGardenType.TYPE_COMIC,
            MagnetGardenType.TYPE_HK,
            MagnetGardenType.TYPE_JP,
            MagnetGardenType.TYPE_MUSIC,
            MagnetGardenType.TYPE_MUSIC_ANIMATION,
            MagnetGardenType.TYPE_MUSIC_COMIC,
            MagnetGardenType.TYPE_MUSIC_POP,
            MagnetGardenType.TYPE_DRAMA,
            MagnetGardenType.TYPE_RAW,
            MagnetGardenType.TYPE_GAME,
            MagnetGardenType.TYPE_GAME_PC,
            MagnetGardenType.TYPE_GAME_TV,
            MagnetGardenType.TYPE_GAME_MOBILE,
            MagnetGardenType.TYPE_GAME_NETWORK,
            MagnetGardenType.TYPE_GAME_TOOL,
            MagnetGardenType.TYPE_SPECIAL,
            MagnetGardenType.TYPE_OTHER,
        ).map { ComposeTextTab(it, labelText = MagnetGardenType.string(it)) }
            .toPersistentList()
    }

    val magnetGardenTeams by lazy {
        listOf(
            MagnetGardenTeam.TEAM_ALL,
            MagnetGardenTeam.TEAM_DMM,
            MagnetGardenTeam.TEAM_DORAEMON,
            MagnetGardenTeam.TEAM_NC_RAWS,
            MagnetGardenTeam.TEAM_MANGA_HOUSE,
            MagnetGardenTeam.TEAM_LILITH,
            MagnetGardenTeam.TEAM_MOXING,
            MagnetGardenTeam.TEAM_YINGDU,
            MagnetGardenTeam.TEAM_TMOON,
            MagnetGardenTeam.TEAM_JIYING,
            MagnetGardenTeam.TEAM_LOLIHOUSE,
            MagnetGardenTeam.TEAM_YUHA,
            MagnetGardenTeam.TEAM_HUANYUE,
            MagnetGardenTeam.TEAM_TSDM,
            MagnetGardenTeam.TEAM_DM_GUO,
            MagnetGardenTeam.TEAM_HUANYING,
            MagnetGardenTeam.TEAM_AILIAN,
            MagnetGardenTeam.TEAM_DBD,
            MagnetGardenTeam.TEAM_CC,
            MagnetGardenTeam.TEAM_LOLI,
            MagnetGardenTeam.TEAM_QIAXIA,
            MagnetGardenTeam.TEAM_IET,
            MagnetGardenTeam.TEAM_KAMIGAMI,
            MagnetGardenTeam.TEAM_SHUANGTING,
            MagnetGardenTeam.TEAM_GM,
            MagnetGardenTeam.TEAM_WINDMILL,
            MagnetGardenTeam.TEAM_FLSNOW,
            MagnetGardenTeam.TEAM_MCE,
            MagnetGardenTeam.TEAM_MARUKO,
            MagnetGardenTeam.TEAM_XINGKONG,
            MagnetGardenTeam.TEAM_MENGLAN,
            MagnetGardenTeam.TEAM_LOVEECHO,
            MagnetGardenTeam.TEAM_SWEETSUB,
            MagnetGardenTeam.TEAM_FENGYE,
            MagnetGardenTeam.TEAM_LITTLE_SUBBERS,
            MagnetGardenTeam.TEAM_LIGHTKINGDOM,
            MagnetGardenTeam.TEAM_YUNGUANG,
            MagnetGardenTeam.TEAM_PEA,
            MagnetGardenTeam.TEAM_TRAINER,
            MagnetGardenTeam.TEAM_ZHONGKEN,
            MagnetGardenTeam.TEAM_SW,
            MagnetGardenTeam.TEAM_SILVER_BULLET,
            MagnetGardenTeam.TEAM_WIND_TEMPLE,
            MagnetGardenTeam.TEAM_YWCN,
            MagnetGardenTeam.TEAM_KRL,
            MagnetGardenTeam.TEAM_HUAMENG,
            MagnetGardenTeam.TEAM_BOLO,
            MagnetGardenTeam.TEAM_DONYIN,
            MagnetGardenTeam.TEAM_VCB,
            MagnetGardenTeam.TEAM_DHR,
            MagnetGardenTeam.TEAM_80V08,
            MagnetGardenTeam.TEAM_CAT,
            MagnetGardenTeam.TEAM_LITTLE,
            MagnetGardenTeam.TEAM_AI,
            MagnetGardenTeam.TEAM_LIPU,
            MagnetGardenTeam.TEAM_NIJIGASAKI,
            MagnetGardenTeam.TEAM_ARIA,
            MagnetGardenTeam.TEAM_CONAN,
            MagnetGardenTeam.TEAM_BAIDONG,
            MagnetGardenTeam.TEAM_LENGFAN,
            MagnetGardenTeam.TEAM_AIGU,
            MagnetGardenTeam.TEAM_JICAI,
            MagnetGardenTeam.TEAM_AQUA,
            MagnetGardenTeam.TEAM_WEIYANG,
            MagnetGardenTeam.TEAM_JIELIAN,
            MagnetGardenTeam.TEAM_YEYING,
            MagnetGardenTeam.TEAM_TDRAWS,
            MagnetGardenTeam.TEAM_MENGHUAN,
            MagnetGardenTeam.TEAM_WBX,
            MagnetGardenTeam.TEAM_LIELLA,
            MagnetGardenTeam.TEAM_CIMENG,
            MagnetGardenTeam.TEAM_ACGN,
            MagnetGardenTeam.TEAM_AKINYUNKI,
            MagnetGardenTeam.TEAM_FURRETAR,
            MagnetGardenTeam.TEAM_LINTIANSHI,
            MagnetGardenTeam.TEAM_PREJUDICE,
            MagnetGardenTeam.TEAM_AILIXIYA,
            MagnetGardenTeam.TEAM_HUANCHENG,
            MagnetGardenTeam.TEAM_ATLAS,
            MagnetGardenTeam.TEAM_SAKURA_HANA,
            MagnetGardenTeam.TEAM_XINXING,
            MagnetGardenTeam.TEAM_ZERO,
            MagnetGardenTeam.TEAM_LVCHA,
            MagnetGardenTeam.TEAM_GUANGYU,
            MagnetGardenTeam.TEAM_AMOR,
            MagnetGardenTeam.TEAM_MINGY,
            MagnetGardenTeam.TEAM_XIAOBAI,
            MagnetGardenTeam.TEAM_SAKURA,
            MagnetGardenTeam.TEAM_PMFAN,
            MagnetGardenTeam.TEAM_EME,
            MagnetGardenTeam.TEAM_ALCHEMIST,
            MagnetGardenTeam.TEAM_BLACKROCK,
            MagnetGardenTeam.TEAM_ANI,
            MagnetGardenTeam.TEAM_DBFC,
            MagnetGardenTeam.TEAM_MSB,
        ).map { ComposeTextTab(it, labelText = MagnetGardenTeam.string(it)) }
            .toPersistentList()
    }


    private fun createFeature(@FeatureType type: String, icon: DrawableResource) =
        ComposeDrawableTab(
            type = type,
            label = FeatureType.name(type),
            icon = icon
        )
}