@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef

/**
 * ```html
 * <select name="sort_id" id="AdvSearchSort">
 *     <option value="0">全部</option>
 *     <option value="2" style="color: red" selected="selected">动画</option>
 *     <option value="31" style="color: red">季度全集</option>
 *     <option value="3" style="color: green">漫画</option>
 *     <option value="41" style="color: green">港台原版</option>
 *     <option value="42" style="color: green">日文原版</option>
 *     <option value="4" style="color: purple">音乐</option>
 *     <option value="43" style="color: purple">动漫音乐</option>
 *     <option value="44" style="color: purple">同人音乐</option>
 *     <option value="15" style="color: purple">流行音乐</option>
 *     <option value="6" style="color: blue">日剧</option>
 *     <option value="7" style="color: orange">ＲＡＷ</option>
 *     <option value="9" style="color: #0eb9e7">游戏</option>
 *     <option value="17" style="color: #0eb9e7">电脑游戏</option>
 *     <option value="18" style="color: #0eb9e7">电视游戏</option>
 *     <option value="19" style="color: #0eb9e7">掌机游戏</option>
 *     <option value="20" style="color: #0eb9e7">网络游戏</option>
 *     <option value="21" style="color: #0eb9e7">游戏周边</option>
 *     <option value="12" style="color: brown">特摄</option>
 *     <option value="1" style="color: black">其他</option>
 * </select>
 * ```
 */
@IntDef(
    MagnetGardenType.TYPE_ALL,
    MagnetGardenType.TYPE_ANIME,
    MagnetGardenType.TYPE_SEASONAL,
    MagnetGardenType.TYPE_COMIC,
    MagnetGardenType.TYPE_HK,
    MagnetGardenType.TYPE_JP,
    MagnetGardenType.TYPE_MUSIC,
    MagnetGardenType.TYPE_MUSIC_ANIMATION,
    MagnetGardenType.TYPE_MUSIC_COMIC,
    MagnetGardenType.TYPE_MUSIC_POP,
    MagnetGardenType.TYPE_DRAMA,
    MagnetGardenType.TYPE_RAW,
    MagnetGardenType.TYPE_GAME,
    MagnetGardenType.TYPE_GAME_PC,
    MagnetGardenType.TYPE_GAME_TV,
    MagnetGardenType.TYPE_GAME_MOBILE,
    MagnetGardenType.TYPE_GAME_NETWORK,
    MagnetGardenType.TYPE_GAME_TOOL,
    MagnetGardenType.TYPE_SPECIAL,
    MagnetGardenType.TYPE_OTHER,
)
@Retention(AnnotationRetention.SOURCE)
annotation class MagnetGardenType {
    companion object Companion {
        const val TYPE_ALL = 0
        const val TYPE_ANIME = 2
        const val TYPE_SEASONAL = 31
        const val TYPE_COMIC = 3
        const val TYPE_HK = 41
        const val TYPE_JP = 42
        const val TYPE_MUSIC = 4
        const val TYPE_MUSIC_ANIMATION = 43
        const val TYPE_MUSIC_COMIC = 44
        const val TYPE_MUSIC_POP = 45
        const val TYPE_DRAMA = 6
        const val TYPE_RAW = 7
        const val TYPE_GAME = 9
        const val TYPE_GAME_PC = 17
        const val TYPE_GAME_TV = 18
        const val TYPE_GAME_MOBILE = 19
        const val TYPE_GAME_NETWORK = 20
        const val TYPE_GAME_TOOL = 21
        const val TYPE_SPECIAL = 12
        const val TYPE_OTHER = 1

        fun string(@MagnetGardenType type: Int): String {
            return when (type) {
                TYPE_ALL -> "全部分类"
                TYPE_ANIME -> "动画"
                TYPE_SEASONAL -> "季度全集"
                TYPE_COMIC -> "漫画"
                TYPE_HK -> "港台原版"
                TYPE_JP -> "日文原版"
                TYPE_MUSIC -> "音乐"
                TYPE_MUSIC_ANIMATION -> "动漫音乐"
                TYPE_MUSIC_COMIC -> "同人音乐"
                TYPE_MUSIC_POP -> "流行音乐"
                TYPE_DRAMA -> "日剧"
                TYPE_RAW -> "ＲＡＷ"
                TYPE_GAME -> "游戏"
                TYPE_GAME_PC -> "电脑游戏"
                TYPE_GAME_TV -> "电视游戏"
                TYPE_GAME_MOBILE -> "掌机游戏"
                TYPE_GAME_NETWORK -> "网络游戏"
                TYPE_GAME_TOOL -> "游戏周边"
                TYPE_SPECIAL -> "特摄"
                TYPE_OTHER -> "其他"
                else -> "全部"
            }
        }
    }
}