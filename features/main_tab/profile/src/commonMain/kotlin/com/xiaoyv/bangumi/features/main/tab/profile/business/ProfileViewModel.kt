package com.xiaoyv.bangumi.features.main.tab.profile.business

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_blog
import com.xiaoyv.bangumi.core_resource.resources.global_collection
import com.xiaoyv.bangumi.core_resource.resources.global_friend
import com.xiaoyv.bangumi.core_resource.resources.global_group
import com.xiaoyv.bangumi.core_resource.resources.global_index
import com.xiaoyv.bangumi.core_resource.resources.global_mono
import com.xiaoyv.bangumi.core_resource.resources.index_my_collection
import com.xiaoyv.bangumi.core_resource.resources.index_my_create
import com.xiaoyv.bangumi.core_resource.resources.profile_time_machine
import com.xiaoyv.bangumi.core_resource.resources.type_topic_crt
import com.xiaoyv.bangumi.core_resource.resources.type_topic_person
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.ProfileMenu
import com.xiaoyv.bangumi.shared.core.types.ProfileTab
import com.xiaoyv.bangumi.shared.core.types.list.ListIndexType
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf

/**
 * [ProfileViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class ProfileViewModel(savedStateHandle: SavedStateHandle) :
    BaseViewModel<ProfileState, ProfileSideEffect, ProfileEvent.Action>(savedStateHandle) {

    override fun initSate(onCreate: Boolean) = ProfileState(
        tabs = persistentListOf(
            ComposeTextTab(ProfileTab.COLLECTION, label = Res.string.global_collection),
            ComposeTextTab(ProfileTab.MONO, label = Res.string.global_mono),
            ComposeTextTab(ProfileTab.BLOG, label = Res.string.global_blog),
            ComposeTextTab(ProfileTab.INDEX, label = Res.string.global_index),
            ComposeTextTab(ProfileTab.GROUP, label = Res.string.global_group),
            ComposeTextTab(ProfileTab.FRIEND, label = Res.string.global_friend),
        ),
        topBarMenu = persistentListOf(
            ComposeTextTab(ProfileMenu.TIME_MACHINE, label = Res.string.profile_time_machine),
        ),
        monoTypeFilters = persistentListOf(
            ComposeTextTab(MonoType.CHARACTER, Res.string.type_topic_crt),
            ComposeTextTab(MonoType.PERSON, Res.string.type_topic_person),
        ),
        indexFilters = persistentListOf(
            ComposeTextTab(ListIndexType.USER_CREATE, Res.string.index_my_create),
            ComposeTextTab(ListIndexType.USER_COLLECTION, Res.string.index_my_collection),
        ),
    )

    override fun onEvent(event: ProfileEvent.Action) {
        when (event) {
            is ProfileEvent.Action.OnChangeSubjectTypeFilter -> onChangeSubjectTypeFilter(event.type)
            is ProfileEvent.Action.OnChangeCollectionTypeFilter -> onChangeCollectionTypeFilter(event.type)
            is ProfileEvent.Action.OnChangeCollectionSortFilter -> onChangeCollectionSortFilter(event.type)
        }
    }

    private fun onChangeCollectionSortFilter(type: String) = action {
        reduceContent { state.copy(selectedCollectSort = type) }
    }

    private fun onChangeCollectionTypeFilter(type: Int) = action {
        reduceContent { state.copy(selectedCollectType = type) }
    }

    private fun onChangeSubjectTypeFilter(type: Int) = action {
        reduceContent { state.copy(selectedSubjectType = type) }
    }
}