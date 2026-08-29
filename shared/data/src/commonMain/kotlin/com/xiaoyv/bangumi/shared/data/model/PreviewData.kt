package com.xiaoyv.bangumi.shared.data.model

import androidx.paging.PagingData
import com.xiaoyv.bangumi.shared.core.types.IndexCatType
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeCollectionInfo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeRating
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndexRelated
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectInterest
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeNotice
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import kotlinx.coroutines.flow.flowOf

val PreviewComposeSubject = ComposeSubject(
    name = "葬送的芙莉莲",
    nameCn = "葬送的芙莉莲",
    summary = "勇者拯救魔王勇者拯救魔王勇者拯救魔王勇者拯救魔王勇者拯救魔王勇者拯救魔王勇者拯救魔王勇者拯救魔王勇者拯救魔王勇者拯救魔王",
    eps = 12,
    id = 1000,
    collection = ComposeCollectionInfo(
        wish = 100,
        collect = 100,
        doing = 100
    ),
    rating = ComposeRating(rank = 100, total = 100, score = 9.9),
)

val PreviewComposeSubjectInterest = ComposeSubjectInterest(
    comment = "随机评论内容",
    epStatus = 6
)

val PreviewComposeUser = ComposeUser(
    id = 1,
    nickname = "tom",
    username = "tiny"
)

val PreviewComposeNotice = ComposeNotice(
    title = "随机评论内容",
    sender = PreviewComposeUser,
    createdAt = 1234567890000
)

val PreviewComposeReply = ComposeReply(
    id = 1,
    content = "随机评论内容",
    user = PreviewComposeUser,
    createdAt = 1234567890000
)

val PreviewComposeIndexRelated = ComposeIndexRelated(
    id = 1111,
    cat = IndexCatType.SUBJECT,
    comment = "这是一条目录备注，用于说明为什么把这个条目收录在这里。",
    subject = PreviewComposeSubject
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
