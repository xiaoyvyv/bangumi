package com.xiaoyv.bangumi.shared.data.usecase

import com.xiaoyv.bangumi.shared.data.repository.SubjectRepository

/**
 * [SubjectRepoUseCase]
 *
 * @since 2025/5/18
 */
class SubjectRepoUseCase(private val subjectRepository: SubjectRepository) {

    private val sortRelation = arrayOf(
        "前传",
        "续集",
        "总集篇",
        "番外篇",
        "相同世界观",
        "动画",
        "书籍",
        "画集",
        "单行本",
        "游戏",
        "三次元",
        "原声集",
        "片头曲",
        "片尾曲",
        "不同版本",
        "其他"
    )
    private val sortCharacterRelation = arrayOf(
        "主角", "配角"
    )

    private val sortPersonRelation = arrayOf(
        "原作",
        "导演",
        "脚本",
        "分镜",
        "演出",
        "音乐"
    )
}