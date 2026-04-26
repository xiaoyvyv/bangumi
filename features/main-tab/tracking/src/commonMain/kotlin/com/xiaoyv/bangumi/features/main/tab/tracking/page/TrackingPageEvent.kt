package com.xiaoyv.bangumi.features.main.tab.tracking.page

import com.xiaoyv.bangumi.shared.core.types.CollectionEpisodeType
import com.xiaoyv.bangumi.shared.data.model.request.CollectionSubjectUpdate
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEpisode
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject

sealed class TrackingPageEvent {

    sealed class Action : TrackingPageEvent() {
        data class OnUpdateEpisodeCollection(
            val subject: ComposeSubject,
            val episodes: List<ComposeEpisode>,
            @field:CollectionEpisodeType val type: Int,
        ) : Action()

        data class OnUpdateSubjectCollection(
            val subject: ComposeSubject,
            val update: CollectionSubjectUpdate,
        ) : Action()
    }
}