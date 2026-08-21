package com.xiaoyv.bangumi.features.subject.detail.page.rant

import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.reaction.ComposeReaction

sealed class SubjectDetailRantEvent {
    data class OnReactionClick(val comment: ComposeReply, val reaction: ComposeReaction) : SubjectDetailRantEvent()
}