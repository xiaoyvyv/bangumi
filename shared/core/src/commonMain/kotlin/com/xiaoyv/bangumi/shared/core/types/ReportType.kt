@file:Suppress("unused", "SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.action_report_menu_other
import com.xiaoyv.bangumi.core_resource.resources.action_report_type_blog
import com.xiaoyv.bangumi.core_resource.resources.action_report_type_blog_comment
import com.xiaoyv.bangumi.core_resource.resources.action_report_type_character_comment
import com.xiaoyv.bangumi.core_resource.resources.action_report_type_ep_comment
import com.xiaoyv.bangumi.core_resource.resources.action_report_type_group_article
import com.xiaoyv.bangumi.core_resource.resources.action_report_type_group_article_comment
import com.xiaoyv.bangumi.core_resource.resources.action_report_type_index
import com.xiaoyv.bangumi.core_resource.resources.action_report_type_index_comment
import com.xiaoyv.bangumi.core_resource.resources.action_report_type_person_comment
import com.xiaoyv.bangumi.core_resource.resources.action_report_type_subject_article
import com.xiaoyv.bangumi.core_resource.resources.action_report_type_subject_article_comment
import com.xiaoyv.bangumi.core_resource.resources.action_report_type_timeline
import com.xiaoyv.bangumi.core_resource.resources.action_report_type_timeline_comment
import com.xiaoyv.bangumi.core_resource.resources.action_report_type_user
import org.jetbrains.compose.resources.StringResource

/**
 * Class: [ReportType]
 *
 * @author why
 * @since 11/25/23
 */
@IntDef(
    ReportType.UNKNOWN,
    ReportType.USER,
    ReportType.GROUP_ARTICLE,
    ReportType.GROUP_ARTICLE_COMMENT,
    ReportType.SUBJECT_ARTICLE,
    ReportType.SUBJECT_ARTICLE_COMMENT,
    ReportType.EP_COMMENT,
    ReportType.CHARACTER_COMMENT,
    ReportType.PERSON_COMMENT,
    ReportType.BLOG,
    ReportType.BLOG_COMMENT,
    ReportType.TIMELINE,
    ReportType.TIMELINE_COMMENT,
    ReportType.INDEX,
    ReportType.INDEX_COMMENT
)
@Retention(AnnotationRetention.SOURCE)
annotation class ReportType {

    companion object {
        const val UNKNOWN = 0

        /** 用户 */
        const val USER = 6

        /** 小组话题 */
        const val GROUP_ARTICLE = 7

        /** 小组回复 */
        const val GROUP_ARTICLE_COMMENT = 8

        /** 条目话题 */
        const val SUBJECT_ARTICLE = 9

        /** 条目回复 */
        const val SUBJECT_ARTICLE_COMMENT = 10

        /** 章节回复 */
        const val EP_COMMENT = 11

        /** 角色回复 */
        const val CHARACTER_COMMENT = 12

        /** 人物回复 */
        const val PERSON_COMMENT = 13

        /** 日志 */
        const val BLOG = 14

        /** 日志回复 */
        const val BLOG_COMMENT = 15

        /** 时间线 */
        const val TIMELINE = 16

        /** 时间线回复 */
        const val TIMELINE_COMMENT = 17

        /** 目录 */
        const val INDEX = 18

        /** 目录回复 */
        const val INDEX_COMMENT = 19

        fun stringRes(@ReportType type: Int): StringResource {
            return when (type) {
                USER -> Res.string.action_report_type_user
                GROUP_ARTICLE -> Res.string.action_report_type_group_article
                GROUP_ARTICLE_COMMENT -> Res.string.action_report_type_group_article_comment
                SUBJECT_ARTICLE -> Res.string.action_report_type_subject_article
                SUBJECT_ARTICLE_COMMENT -> Res.string.action_report_type_subject_article_comment
                EP_COMMENT -> Res.string.action_report_type_ep_comment
                CHARACTER_COMMENT -> Res.string.action_report_type_character_comment
                PERSON_COMMENT -> Res.string.action_report_type_person_comment
                BLOG -> Res.string.action_report_type_blog
                BLOG_COMMENT -> Res.string.action_report_type_blog_comment
                TIMELINE -> Res.string.action_report_type_timeline
                TIMELINE_COMMENT -> Res.string.action_report_type_timeline_comment
                INDEX -> Res.string.action_report_type_index
                INDEX_COMMENT -> Res.string.action_report_type_index_comment
                else -> Res.string.action_report_menu_other
            }
        }
    }
}
