package com.xiaoyv.common.helper

import com.xiaoyv.common.api.parser.entity.CommentTreeEntity

/**
 * Class: [CommentPaginationHelper]
 */
class CommentPaginationHelper(private var comments: List<CommentTreeEntity>) {

    /**
     * 分页加载评论
     */
    fun loadComments(page: Int, size: Int, isDesc: Boolean): List<CommentTreeEntity> {
        val startIndex = (page - 1) * size
        val endIndex = startIndex + size

        if (startIndex >= comments.size) {
            // 如果起始索引超出评论列表范围，返回空列表表示没有更多评论可加载
            return emptyList()
        }

        return if (isDesc) {
            comments.reversed().subList(startIndex, endIndex.coerceAtMost(comments.size))
        } else {
            comments.subList(startIndex, endIndex.coerceAtMost(comments.size))
        }
    }
}