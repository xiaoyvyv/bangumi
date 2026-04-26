package com.xiaoyv.bangumi

import com.xiaoyv.bangumi.features.almanac.business.AlmanacViewModel
import com.xiaoyv.bangumi.features.article.business.ArticleViewModel
import com.xiaoyv.bangumi.features.blog.page.business.BlogPageViewModel
import com.xiaoyv.bangumi.features.detect.business.ReceiveViewModel
import com.xiaoyv.bangumi.features.dollars.business.DollarsViewModel
import com.xiaoyv.bangumi.features.friend.business.FriendViewModel
import com.xiaoyv.bangumi.features.gallery.business.GalleryViewModel
import com.xiaoyv.bangumi.features.garden.business.GardenViewModel
import com.xiaoyv.bangumi.features.groups.detail.business.GroupsDetailViewModel
import com.xiaoyv.bangumi.features.groups.page.business.GroupsPageViewModel
import com.xiaoyv.bangumi.features.index.detail.business.IndexDetailViewModel
import com.xiaoyv.bangumi.features.index.detail.page.IndexDetailPageViewModel
import com.xiaoyv.bangumi.features.index.page.dialog.IndexDialogViewModel
import com.xiaoyv.bangumi.features.index.page.page.IndexPageViewModel
import com.xiaoyv.bangumi.features.main.business.MainViewModel
import com.xiaoyv.bangumi.features.main.tab.home.business.CalendarViewModel
import com.xiaoyv.bangumi.features.main.tab.home.business.HomeViewModel
import com.xiaoyv.bangumi.features.main.tab.home.page.group.HomeGroupViewModel
import com.xiaoyv.bangumi.features.main.tab.home.page.mono.HomeMonoViewModel
import com.xiaoyv.bangumi.features.main.tab.newest.business.NewestViewModel
import com.xiaoyv.bangumi.features.main.tab.profile.business.ProfileViewModel
import com.xiaoyv.bangumi.features.main.tab.timeline.business.TimelineViewModel
import com.xiaoyv.bangumi.features.main.tab.topic.business.TopicViewModel
import com.xiaoyv.bangumi.features.main.tab.topic.page.TopicPageViewModel
import com.xiaoyv.bangumi.features.main.tab.tracking.business.TrackingViewModel
import com.xiaoyv.bangumi.features.main.tab.tracking.page.TrackingPageViewModel
import com.xiaoyv.bangumi.features.message.business.MessageMainViewModel
import com.xiaoyv.bangumi.features.message.chat.business.MessageChatViewModel
import com.xiaoyv.bangumi.features.mikan.detail.business.MikanDetailViewModel
import com.xiaoyv.bangumi.features.mikan.studio.business.MikanStudioViewModel
import com.xiaoyv.bangumi.features.mono.browser.business.MonoBrowserViewModel
import com.xiaoyv.bangumi.features.mono.detail.business.MonoDetailViewModel
import com.xiaoyv.bangumi.features.mono.detail.page.MonoDetailCastsViewModel
import com.xiaoyv.bangumi.features.mono.page.business.MonoPageViewModel
import com.xiaoyv.bangumi.features.notification.business.NotificationViewModel
import com.xiaoyv.bangumi.features.pixiv.login.business.PixivLoginViewModel
import com.xiaoyv.bangumi.features.pixiv.main.business.PixivMainViewModel
import com.xiaoyv.bangumi.features.preivew.album.business.PreviewAlbumViewModel
import com.xiaoyv.bangumi.features.preivew.gallery.business.PreviewTextViewModel
import com.xiaoyv.bangumi.features.preivew.main.business.PreviewMainViewModel
import com.xiaoyv.bangumi.features.search.input.business.SearchInputViewModel
import com.xiaoyv.bangumi.features.search.result.business.SearchResultViewModel
import com.xiaoyv.bangumi.features.settings.account.business.SettingsAccountViewModel
import com.xiaoyv.bangumi.features.settings.bar.business.SettingsBarViewModel
import com.xiaoyv.bangumi.features.settings.block.business.SettingsBlockViewModel
import com.xiaoyv.bangumi.features.settings.live2d.business.SettingsLive2dViewModel
import com.xiaoyv.bangumi.features.settings.main.business.SettingsMainViewModel
import com.xiaoyv.bangumi.features.settings.network.business.SettingsNetworkViewModel
import com.xiaoyv.bangumi.features.settings.privacy.business.SettingsPrivacyViewModel
import com.xiaoyv.bangumi.features.settings.translate.business.SettingsTranslateViewModel
import com.xiaoyv.bangumi.features.settings.ui.business.SettingsUiViewModel
import com.xiaoyv.bangumi.features.sign.sign_in.business.SignInViewModel
import com.xiaoyv.bangumi.features.subject.browser.business.SubjectBrowserViewModel
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailViewModel
import com.xiaoyv.bangumi.features.subject.detail.page.SubjectDetailChartViewModel
import com.xiaoyv.bangumi.features.subject.page.business.SubjectPageViewModel
import com.xiaoyv.bangumi.features.tag.detail.business.TagDetailViewModel
import com.xiaoyv.bangumi.features.tag.page.business.TagPageViewModel
import com.xiaoyv.bangumi.features.timeline.detail.business.TimelineDetailViewModel
import com.xiaoyv.bangumi.features.timeline.page.business.TimelinePageViewModel
import com.xiaoyv.bangumi.features.timeline.page.web.TimelineWebPageViewModel
import com.xiaoyv.bangumi.features.topic.detail.business.TopicDetailViewModel
import com.xiaoyv.bangumi.features.user.business.UserViewModel
import com.xiaoyv.bangumi.features.web.business.WebViewModel
import com.xiaoyv.bangumi.shared.data.di.dataModules
import com.xiaoyv.bangumi.shared.data.manager.shared.SharedViewModel
import com.xiaoyv.bangumi.shared.data.model.request.IndexTarget
import com.xiaoyv.bangumi.shared.data.model.request.list.album.ListAlbumParam
import com.xiaoyv.bangumi.shared.data.model.request.list.blog.ListBlogParam
import com.xiaoyv.bangumi.shared.data.model.request.list.group.ListGroupParam
import com.xiaoyv.bangumi.shared.data.model.request.list.index.ListIndexParam
import com.xiaoyv.bangumi.shared.data.model.request.list.index.ListIndexRelatedParam
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.ListMonoParam
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.ListPersonCastParam
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.ListSubjectParam
import com.xiaoyv.bangumi.shared.data.model.request.list.tag.ListTagParam
import com.xiaoyv.bangumi.shared.data.model.request.list.timeline.ListTimelineParam
import com.xiaoyv.bangumi.shared.data.model.request.list.topic.ListTopicParam
import com.xiaoyv.bangumi.shared.data.model.request.list.user.ListUserParam
import com.xiaoyv.bangumi.shared.ui.component.dialog.comment.CommentDialogAnchor
import com.xiaoyv.bangumi.shared.ui.component.dialog.comment.CommentViewModel
import org.koin.core.KoinApplication
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun KoinApplication.initializeKoin() = modules(
    commonModule,
    navigationModule,
    *dataModules
)

val commonModule = module {
    viewModelOf(::SharedViewModel)

    viewModelOf(::MainViewModel)
    viewModelOf(::AlmanacViewModel)
    viewModelOf(::SignInViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::TimelineViewModel)
    viewModelOf(::TopicViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::TrackingViewModel)
    viewModelOf(::MessageMainViewModel)
    viewModelOf(::NotificationViewModel)
    viewModelOf(::SettingsAccountViewModel)
    viewModelOf(::SettingsBarViewModel)
    viewModelOf(::SettingsBlockViewModel)
    viewModelOf(::SettingsLive2dViewModel)
    viewModelOf(::SettingsMainViewModel)
    viewModelOf(::SettingsNetworkViewModel)
    viewModelOf(::SettingsPrivacyViewModel)
    viewModelOf(::SettingsTranslateViewModel)
    viewModelOf(::SettingsUiViewModel)
    viewModelOf(::SearchInputViewModel)
    viewModelOf(::SearchResultViewModel)
    viewModelOf(::SubjectDetailViewModel)
    viewModelOf(::ReceiveViewModel)
    viewModelOf(::MonoDetailViewModel)
    viewModelOf(::MonoBrowserViewModel)
    viewModelOf(::MikanStudioViewModel)
    viewModelOf(::MikanDetailViewModel)
    viewModelOf(::GalleryViewModel)
    viewModelOf(::WebViewModel)
    viewModelOf(::ArticleViewModel)
    viewModelOf(::NewestViewModel)
    viewModelOf(::DollarsViewModel)
    viewModelOf(::HomeMonoViewModel)
    viewModelOf(::MessageChatViewModel)
    viewModelOf(::HomeGroupViewModel)
    viewModelOf(::IndexDetailViewModel)
    viewModelOf(::GroupsDetailViewModel)
    viewModelOf(::PreviewTextViewModel)
    viewModelOf(::UserViewModel)
    viewModelOf(::TimelineDetailViewModel)
    viewModelOf(::CalendarViewModel)
    viewModelOf(::TagDetailViewModel)
    viewModelOf(::SubjectBrowserViewModel)
    viewModelOf(::PreviewMainViewModel)
    viewModelOf(::PixivMainViewModel)
    viewModelOf(::PixivLoginViewModel)
    viewModelOf(::GardenViewModel)
    viewModelOf(::TopicDetailViewModel)

    viewModel { (type: Int) ->
        TrackingPageViewModel(
            stateHandle = get(),
            type = type,
            collectionRepository = get(),
            personalStateStore = get()
        )
    }

    viewModel { (anchor: CommentDialogAnchor) ->
        CommentViewModel(
            stateHandle = get(),
            ugcRepository = get(),
            choreRepository = get(),
            dialogAnchor = anchor
        )
    }

    viewModel { (type: String) ->
        TopicPageViewModel(
            savedStateHandle = get(),
            ugcRepository = get(),
            type = type
        )
    }

    viewModel { (param: ListTimelineParam) ->
        TimelineWebPageViewModel(
            savedStateHandle = get(),
            ugcRepository = get(),
            param = param,
        )
    }

    viewModel { (param: ListTimelineParam) ->
        TimelinePageViewModel(
            savedStateHandle = get(),
            ugcRepository = get(),
            param = param,
        )
    }

    viewModel { (param: ListSubjectParam) ->
        SubjectPageViewModel(
            savedStateHandle = get(),
            subjectRepository = get(),
            param = param
        )
    }

    viewModel { (param: ListMonoParam) ->
        MonoPageViewModel(
            savedStateHandle = get(),
            monoRepository = get(),
            param = param
        )
    }

    viewModel { (param: ListBlogParam) ->
        BlogPageViewModel(
            savedStateHandle = get(),
            ugcRepository = get(),
            param = param
        )
    }

    viewModel { (param: ListIndexParam) ->
        IndexPageViewModel(
            ugcRepository = get(),
            param = param
        )
    }

    viewModel { (target: IndexTarget) ->
        IndexDialogViewModel(
            savedStateHandle = get(),
            indexRepository = get(),
            userManager = get(),
            target = target
        )
    }

    viewModel { (param: ListUserParam) ->
        FriendViewModel(
            savedStateHandle = get(),
            userRepository = get(),
            param = param
        )
    }

    viewModel { (param: ListGroupParam) ->
        GroupsPageViewModel(
            savedStateHandle = get(),
            groupRepository = get(),
            param = param
        )
    }

    viewModel { (param: ListTopicParam) ->
        com.xiaoyv.bangumi.features.topic.page.business.TopicPageViewModel(
            savedStateHandle = get(),
            param = param,
            topicRepository = get()
        )
    }

    viewModel { (param: ListAlbumParam) ->
        PreviewAlbumViewModel(
            savedStateHandle = get(),
            param = param,
            imageRepository = get()
        )
    }

    viewModel { (param: ListPersonCastParam) ->
        MonoDetailCastsViewModel(
            param = param,
            monoRepository = get()
        )
    }
    viewModel { (subjectId: Long) ->
        SubjectDetailChartViewModel(
            savedStateHandle = get(),
            subjectRepository = get(),
            subjectId = subjectId
        )
    }

    viewModel { (param: ListTagParam) ->
        TagPageViewModel(
            savedStateHandle = get(),
            subjectRepository = get(),
            param = param,
        )
    }

    viewModel { (param: ListIndexRelatedParam) ->
        IndexDetailPageViewModel(
            param = param,
            savedStateHandle = get(),
            ugcRepository = get()
        )
    }
}