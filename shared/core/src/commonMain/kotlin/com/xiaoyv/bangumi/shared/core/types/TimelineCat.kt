package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.timeline_blog
import com.xiaoyv.bangumi.core_resource.resources.timeline_daily
import com.xiaoyv.bangumi.core_resource.resources.timeline_index
import com.xiaoyv.bangumi.core_resource.resources.timeline_mono
import com.xiaoyv.bangumi.core_resource.resources.timeline_progress
import com.xiaoyv.bangumi.core_resource.resources.timeline_status
import com.xiaoyv.bangumi.core_resource.resources.timeline_subject
import com.xiaoyv.bangumi.core_resource.resources.timeline_wiki
import com.xiaoyv.bangumi.core_resource.resources.timeline_window
import com.xiaoyv.bangumi.core_resource.resources.type_unknown
import org.jetbrains.compose.resources.StringResource

@IntDef(
    TimelineCat.UNKNOWN,
    TimelineCat.DAILY,
    TimelineCat.WIKI,
    TimelineCat.SUBJECT,
    TimelineCat.PROGRESS,
    TimelineCat.STATUS,
    TimelineCat.BLOG,
    TimelineCat.INDEX,
    TimelineCat.MONO,
    TimelineCat.WINDOW,
)
@Retention(AnnotationRetention.SOURCE)
annotation class TimelineCat {
    companion object {
        const val UNKNOWN = 0
        const val DAILY = 1        // 日常行为
        const val WIKI = 2         // 维基操作
        const val SUBJECT = 3      // 收藏条目
        const val PROGRESS = 4     // 收视进度
        const val STATUS = 5       // 状态
        const val BLOG = 6         // 日志
        const val INDEX = 7        // 目录
        const val MONO = 8         // 人物
        const val WINDOW = 9       // 天窗

        fun string(@TimelineCat type: Int): StringResource {
            return when (type) {
                DAILY -> Res.string.timeline_daily
                WIKI -> Res.string.timeline_wiki
                SUBJECT -> Res.string.timeline_subject
                PROGRESS -> Res.string.timeline_progress
                STATUS -> Res.string.timeline_status
                BLOG -> Res.string.timeline_blog
                INDEX -> Res.string.timeline_index
                MONO -> Res.string.timeline_mono
                WINDOW -> Res.string.timeline_window
                else -> Res.string.type_unknown
            }
        }
    }
}


/* ============================== Daily ============================== */

@IntDef(
    TimelineDailyAction.UNKNOWN,
    TimelineDailyAction.REGISTER,
    TimelineDailyAction.ADD_FRIEND,
    TimelineDailyAction.JOIN_GROUP,
    TimelineDailyAction.CREATE_GROUP,
    TimelineDailyAction.JOIN_PARK,
)
@Retention(AnnotationRetention.SOURCE)
annotation class TimelineDailyAction {
    /**
     * - 0 = 神秘的行动
     * - 1 = 注册
     * - 2 = 添加好友
     * - 3 = 加入小组
     * - 4 = 创建小组
     * - 5 = 加入乐园
     */
    companion object {
        const val UNKNOWN = 0
        const val REGISTER = 1
        const val ADD_FRIEND = 2
        const val JOIN_GROUP = 3
        const val CREATE_GROUP = 4
        const val JOIN_PARK = 5
    }
}

/* ============================== Wiki ============================== */

@IntDef(
    TimelineWikiAction.ADD_BOOK,
    TimelineWikiAction.ADD_ANIME,
    TimelineWikiAction.ADD_MUSIC,
    TimelineWikiAction.ADD_GAME,
    TimelineWikiAction.ADD_BOOK_SERIES,
    TimelineWikiAction.ADD_VIDEO,
)
@Retention(AnnotationRetention.SOURCE)
annotation class TimelineWikiAction {
    /**
     * - 1 = 添加了新书
     * - 2 = 添加了新动画
     * - 3 = 添加了新唱片
     * - 4 = 添加了新游戏
     * - 5 = 添加了新图书系列
     * - 6 = 添加了新影视
     */
    companion object {
        const val ADD_BOOK = 1
        const val ADD_ANIME = 2
        const val ADD_MUSIC = 3
        const val ADD_GAME = 4
        const val ADD_BOOK_SERIES = 5
        const val ADD_VIDEO = 6
    }
}

/* ============================== Subject (Single) ============================== */

@IntDef(
    TimelineSubjectAction.WISH_READ,
    TimelineSubjectAction.WISH_WATCH,
    TimelineSubjectAction.WISH_LISTEN,
    TimelineSubjectAction.WISH_PLAY,
    TimelineSubjectAction.DONE_READ,
    TimelineSubjectAction.DONE_WATCH,
    TimelineSubjectAction.DONE_LISTEN,
    TimelineSubjectAction.DONE_PLAY,
    TimelineSubjectAction.DOING_READ,
    TimelineSubjectAction.DOING_WATCH,
    TimelineSubjectAction.DOING_LISTEN,
    TimelineSubjectAction.DOING_PLAY,
    TimelineSubjectAction.ON_HOLD,
    TimelineSubjectAction.DROPPED,
)
@Retention(AnnotationRetention.SOURCE)
annotation class TimelineSubjectAction {
    /**
     * - 1  = 想读
     * - 2  = 想看
     * - 3  = 想听
     * - 4  = 想玩
     * - 5  = 读过
     * - 6  = 看过
     * - 7  = 听过
     * - 8  = 玩过
     * - 9  = 在读
     * - 10 = 在看
     * - 11 = 在听
     * - 12 = 在玩
     * - 13 = 搁置了
     * - 14 = 抛弃了
     */
    companion object {
        const val WISH_READ = 1
        const val WISH_WATCH = 2
        const val WISH_LISTEN = 3
        const val WISH_PLAY = 4
        const val DONE_READ = 5
        const val DONE_WATCH = 6
        const val DONE_LISTEN = 7
        const val DONE_PLAY = 8
        const val DOING_READ = 9
        const val DOING_WATCH = 10
        const val DOING_LISTEN = 11
        const val DOING_PLAY = 12
        const val ON_HOLD = 13
        const val DROPPED = 14
    }
}


/* ============================== Progress ============================== */

@IntDef(
    TimelineProgressAction.BATCH_DONE,
    TimelineProgressAction.WISH,
    TimelineProgressAction.DONE,
    TimelineProgressAction.DROPPED,
)
@Retention(AnnotationRetention.SOURCE)
annotation class TimelineProgressAction {
    /**
     * - 0 = batch(完成)
     * - 1 = 想看
     * - 2 = 看过
     * - 3 = 抛弃
     */
    companion object {
        const val BATCH_DONE = 0
        const val WISH = 1
        const val DONE = 2
        const val DROPPED = 3
    }
}

/* ============================== Status ============================== */

@IntDef(
    TimelineStatusAction.UPDATE_SIGN,
    TimelineStatusAction.COMMENT,
    TimelineStatusAction.RENAME,
)
@Retention(AnnotationRetention.SOURCE)
annotation class TimelineStatusAction {
    /**
     * - 0 = 更新签名
     * - 1 = 吐槽
     * - 2 = 修改昵称
     */
    companion object {
        const val UPDATE_SIGN = 0
        const val COMMENT = 1
        const val RENAME = 2
    }
}

/* ============================== Mono ============================== */

@IntDef(
    TimelineMonoType.CHARACTER,
    TimelineMonoType.PERSON,
)
@Retention(AnnotationRetention.SOURCE)
annotation class TimelineMonoType {
    /**
     * - 1 = 角色
     * - 2 = 人物
     */
    companion object {
        const val CHARACTER = 1
        const val PERSON = 2
    }
}

/* ============================== Doujin ============================== */

@IntDef(
    TimelineDoujinAction.ADD_WORK,
    TimelineDoujinAction.COLLECT_WORK,
    TimelineDoujinAction.CREATE_GROUP,
    TimelineDoujinAction.FOLLOW_GROUP,
    TimelineDoujinAction.FOLLOW_EVENT,
    TimelineDoujinAction.JOIN_EVENT,
)
@Retention(AnnotationRetention.SOURCE)
annotation class TimelineDoujinAction {
    /**
     * - 0 = 添加作品
     * - 1 = 收藏作品
     * - 2 = 创建社团
     * - 3 = 关注社团
     * - 4 = 关注活动
     * - 5 = 参加活动
     */
    companion object {
        const val ADD_WORK = 0
        const val COLLECT_WORK = 1
        const val CREATE_GROUP = 2
        const val FOLLOW_GROUP = 3
        const val FOLLOW_EVENT = 4
        const val JOIN_EVENT = 5
    }
}
