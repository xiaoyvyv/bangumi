@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.core.types

object SubjectCategory {
    // 书籍类型
    enum class SubjectBookCategory(val value: Int) {
        OTHER(0),
        MANGA(1001),    // 漫画
        NOVEL(1002),    // 小说
        ARTBOOK(1003);  // 画集
    }

    // 动画类型
    enum class SubjectAnimeCategory(val value: Int) {
        OTHER(0),
        TV(1),
        OVA(2),
        MOVIE(3),
        WEB(5)
    }

    // 游戏类型
    enum class SubjectGameCategory(val value: Int) {
        OTHER(0),
        GAME(4001),
        SOFTWARE(4002),
        EXPANSION(4003),
        BOARDGAME(4005) // 桌游
    }

    // 影视类型
    enum class SubjectRealCategory(val value: Int) {
        OTHER(0),
        JAPANESE_DRAMA(1),   // 日剧
        WESTERN_DRAMA(2),    // 欧美剧
        CHINESE_DRAMA(3),    // 华语剧
        TV_SERIES(6001),     // 电视剧
        MOVIE(6002),         // 电影
        PERFORMANCE(6003),   // 演出
        VARIETY(6004)        // 综艺
    }
}