@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef


@IntDef(
    NoticeType.UNKNOWN,
    NoticeType.GROUP_TOPIC_REPLY,
    NoticeType.GROUP_POST_REPLY,
    NoticeType.BLOG_REPLY,
    NoticeType.BLOG_POST_REPLY,
    NoticeType.CHARACTER_TOPIC_REPLY,
    NoticeType.CHARACTER_POST_REPLY,
    NoticeType.SUBJECT_TOPIC_REPLY,
    NoticeType.SUBJECT_POST_REPLY,
    NoticeType._9,
    NoticeType.EP_POST_REPLY,
    NoticeType.INDEX_POST_REPLY,
    NoticeType.INDEX_REPLY,
    NoticeType.PERSON_POST_REPLY,
    NoticeType.REQUEST_FRIEND,
    NoticeType.ACCEPT_FRIEND,
    NoticeType._16,
    NoticeType._17,
    NoticeType._18,
    NoticeType._19,
    NoticeType._20,
    NoticeType._21,
    NoticeType.TIMELINE_SAY_REPLY,
    NoticeType.GROUP_TOPIC_AT,
    NoticeType.SUBJECT_TOPIC_AT,
    NoticeType.CHARACTER_POST_AT,
    NoticeType.PERSON_POST_AT,
    NoticeType._27,
    NoticeType.TIMELINE_SAY_AT,
    NoticeType.BLOG_POST_AT,
    NoticeType.EP_POST_AT,
    NoticeType._31,
    NoticeType._32,
    NoticeType._33,
    NoticeType._34,
    NoticeType.SUBJECT_PATCH_ACCEPTED,
    NoticeType.EPISODE_PATCH_ACCEPTED,
    NoticeType.SUBJECT_PATCH_REJECTED,
    NoticeType.EPISODE_PATCH_REJECTED,
    NoticeType.SUBJECT_PATCH_EXPIRED,
    NoticeType.EPISODE_PATCH_EXPIRED,
    NoticeType.CHARACTER_PATCH_ACCEPTED,
    NoticeType.PERSON_PATCH_ACCEPTED,
    NoticeType.CHARACTER_PATCH_REJECTED,
    NoticeType.PERSON_PATCH_REJECTED,
    NoticeType.CHARACTER_PATCH_EXPIRED,
    NoticeType.PERSON_PATCH_EXPIRED,
    NoticeType.SUBJECT_PATCH_REPLY,
    NoticeType.EPISODE_PATCH_REPLY,
    NoticeType.CHARACTER_PATCH_REPLY,
    NoticeType.PERSON_PATCH_REPLY,
)
@Retention(AnnotationRetention.SOURCE)
annotation class NoticeType {
    companion object {
        const val UNKNOWN = 0
        const val GROUP_TOPIC_REPLY = 1
        const val GROUP_POST_REPLY = 2
        const val SUBJECT_TOPIC_REPLY = 3
        const val SUBJECT_POST_REPLY = 4
        const val CHARACTER_TOPIC_REPLY = 5
        const val CHARACTER_POST_REPLY = 6
        const val BLOG_REPLY = 7
        const val BLOG_POST_REPLY = 8
        const val _9 = 9
        const val EP_POST_REPLY = 10
        const val INDEX_REPLY = 11
        const val INDEX_POST_REPLY = 12
        const val PERSON_POST_REPLY = 13

        const val REQUEST_FRIEND = 14
        const val ACCEPT_FRIEND = 15

        /** 未定义 */
        const val _16 = 16
        const val _17 = 17
        const val _18 = 18
        const val _19 = 19
        const val _20 = 20
        const val _21 = 21

        const val TIMELINE_SAY_REPLY = 22

        const val GROUP_TOPIC_AT = 23
        const val SUBJECT_TOPIC_AT = 24
        const val CHARACTER_POST_AT = 25
        const val PERSON_POST_AT = 26
        const val _27 = 27
        const val TIMELINE_SAY_AT = 28
        const val BLOG_POST_AT = 29
        const val EP_POST_AT = 30
        const val _31 = 31
        const val _32 = 32
        const val _33 = 33
        const val _34 = 34

        const val SUBJECT_PATCH_ACCEPTED = 35
        const val EPISODE_PATCH_ACCEPTED = 36
        const val SUBJECT_PATCH_REJECTED = 37
        const val EPISODE_PATCH_REJECTED = 38
        const val SUBJECT_PATCH_EXPIRED = 39
        const val EPISODE_PATCH_EXPIRED = 40
        const val CHARACTER_PATCH_ACCEPTED = 41
        const val PERSON_PATCH_ACCEPTED = 42
        const val CHARACTER_PATCH_REJECTED = 43
        const val PERSON_PATCH_REJECTED = 44
        const val CHARACTER_PATCH_EXPIRED = 45
        const val PERSON_PATCH_EXPIRED = 46
        const val SUBJECT_PATCH_REPLY = 47
        const val EPISODE_PATCH_REPLY = 48
        const val CHARACTER_PATCH_REPLY = 49
        const val PERSON_PATCH_REPLY = 50
    }
}
