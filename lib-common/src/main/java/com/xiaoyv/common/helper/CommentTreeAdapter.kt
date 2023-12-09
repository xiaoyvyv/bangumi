package com.xiaoyv.common.helper

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.xiaoyv.common.api.parser.entity.CommentTreeEntity
import com.xiaoyv.common.databinding.ViewCommentItemBinding
import com.xiaoyv.common.databinding.ViewCommentItemSubBinding
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [CommentTreeAdapter]
 *
 * @author why
 * @since 12/6/23
 */
class CommentTreeAdapter :
    BaseQuickDiffBindingAdapter<CommentTreeEntity, ViewCommentItemBinding>(IdDiffItemCallback()) {
    private val subViewPool = RecycledViewPool()

    override fun BaseQuickBindingHolder<ViewCommentItemBinding>.converted(item: CommentTreeEntity) {
        (binding.rvSub.adapter as CommentTreeSubAdapter).apply {
            submitList(item.topicSubReply)
        }
        binding.ivAvatar.loadImageAnimate(item.userAvatar)
        binding.tvUser.text = item.userName
        binding.tvTime.text = item.time
        binding.tvContent.text = item.replyContent
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): BaseQuickBindingHolder<ViewCommentItemBinding> {
        return super.onCreateViewHolder(context, parent, viewType).apply {
            binding.rvSub.setRecycledViewPool(subViewPool)
            binding.rvSub.adapter = CommentTreeSubAdapter()
        }
    }

    private class CommentTreeSubAdapter :
        BaseQuickDiffBindingAdapter<CommentTreeEntity, ViewCommentItemSubBinding>(IdDiffItemCallback()) {
        override fun BaseQuickBindingHolder<ViewCommentItemSubBinding>.converted(item: CommentTreeEntity) {
            binding.ivAvatar.loadImageAnimate(item.userAvatar)
            binding.tvUser.text = item.userName
            binding.tvTime.text = item.time
            binding.tvContent.text = item.replyContent
        }
    }
}