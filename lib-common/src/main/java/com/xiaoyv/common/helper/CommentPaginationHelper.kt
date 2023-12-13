package com.xiaoyv.common.helper

import com.xiaoyv.common.api.parser.entity.CommentTreeEntity

/**
 * Class: [CommentPaginationHelper]
 */
class CommentPaginationHelper(private var comments: List<CommentTreeEntity> = mutableListOf()) {
    private val sortCache: MutableMap<String, List<CommentTreeEntity>> = mutableMapOf()

    /**
     * 刷新评论数据源
     */
    fun refreshComments(newComments: List<CommentTreeEntity>) {
        comments = newComments
    }

    /**
     * 排序
     */
    private fun loadSortDataSource(sort: String): List<CommentTreeEntity> {
        if (sortCache[sort] == null) {
            val tmp = when (sort) {
                "desc" -> comments.reversed()
                "hot" -> comments.toMutableList().sortedByDescending { it.topicSubReply.size }
                else -> comments
            }
            sortCache[sort] = tmp
            return tmp
        }

        return sortCache[sort].orEmpty()
    }

    /**
     * 分页加载评论
     */
    fun loadComments(page: Int, size: Int, sort: String): List<CommentTreeEntity> {
        val startIndex = (page - 1) * size
        val endIndex = startIndex + size

        if (startIndex >= comments.size) {
            // 如果起始索引超出评论列表范围，返回空列表表示没有更多评论可加载
            return emptyList()
        }

        return loadSortDataSource(sort).subList(startIndex, endIndex.coerceAtMost(comments.size))
    }
}