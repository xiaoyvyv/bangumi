package com.xiaoyv.bangumi.shared.data.model

import androidx.paging.PagingData
import com.xiaoyv.bangumi.shared.core.types.IndexCatType
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeCollection
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeCollectionInfo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeIndexRelated
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeRating
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSubject
import kotlinx.coroutines.flow.flowOf

val PreviewComposeSubject = ComposeSubject(
    name = "葬送的芙莉莲",
    nameCn = "葬送的芙莉莲",
    summary = "勇者拯救魔王",
    eps = 12,
    id = 1000,
    collection = ComposeCollectionInfo(
        wish = 100,
        collect = 100,
        doing = 100,

        ),
    rating = ComposeRating(rank = 100, total = 100, score = 9.9),
)

val PreviewComposeCollection = ComposeCollection(
    subjectId = PreviewComposeSubject.id,
    subject = PreviewComposeSubject,
    subjectType = PreviewComposeSubject.type,
    comment = "随机评论内容",
    epStatus = 6
)

val PreviewComposeIndexRelated = ComposeIndexRelated(
    id = 1111,
    cat = IndexCatType.SUBJECT,
    subject = PreviewComposeSubject
)

val PreviewComposeCollectionLazyItems = flowOf(
    PagingData.from(
        listOf(
            PreviewComposeCollection.copy(subjectId = 1),
            PreviewComposeCollection.copy(subjectId = 2),
            PreviewComposeCollection.copy(subjectId = 3),
            PreviewComposeCollection.copy(subjectId = 4),
            PreviewComposeCollection.copy(subjectId = 5),
        )
    )
)
val PreviewComposeSubjectLazyItems = flowOf(
    PagingData.from(
        listOf(
            PreviewComposeSubject.copy(id = 1),
            PreviewComposeSubject.copy(id = 2),
            PreviewComposeSubject.copy(id = 3),
            PreviewComposeSubject.copy(id = 4),
            PreviewComposeSubject.copy(id = 5),
        )
    )
)

val PreviewComposeIndexRelatedLazyItems = flowOf(
    PagingData.from(
        listOf(
            PreviewComposeIndexRelated.copy(id = 1),
            PreviewComposeIndexRelated.copy(id = 2),
            PreviewComposeIndexRelated.copy(id = 3),
            PreviewComposeIndexRelated.copy(id = 4),
            PreviewComposeIndexRelated.copy(id = 5),
        )
    )
)
