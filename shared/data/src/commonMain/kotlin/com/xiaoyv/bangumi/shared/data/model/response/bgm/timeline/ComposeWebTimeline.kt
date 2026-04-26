package com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import com.xiaoyv.bangumi.shared.core.types.TimelineTab
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeCollection
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeGroup
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class ComposeWebTimeline(
    val timelineType: String = TimelineTab.DYNAMIC,
    val id: String = "",
    val title: AnnotatedString = buildAnnotatedString { },
    val content: AnnotatedString = buildAnnotatedString { },
    val blog: AnnotatedString = buildAnnotatedString { },
    val time: String = "",
    val platform: String = "",
    val user: ComposeUser = ComposeUser.Empty,
    val subjects: SerializeList<ComposeSubject> = persistentListOf(),
    val monos: SerializeList<ComposeMonoDisplay> = persistentListOf(),
    val groups: SerializeList<ComposeGroup> = persistentListOf(),
    val collection: ComposeCollection = ComposeCollection.Empty,
) {
    companion object {
        val Empty: ComposeWebTimeline = ComposeWebTimeline()
    }
}
