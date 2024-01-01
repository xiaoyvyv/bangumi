package com.xiaoyv.bangumi.ui.timeline.page

import android.content.Context
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseDifferAdapter
import com.xiaoyv.bangumi.databinding.FragmentTimelinePageGridBinding
import com.xiaoyv.bangumi.databinding.FragmentTimelinePageMediaBinding
import com.xiaoyv.bangumi.databinding.FragmentTimelinePageTextBinding
import com.xiaoyv.common.api.parser.entity.TimelineEntity
import com.xiaoyv.common.config.annotation.TimelineAdapterType
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.widget.image.AnimeGridImageView
import com.xiaoyv.widget.binder.BaseQuickBindingHolder

/**
 * Class: [TimelinePageAdapter]
 *
 * @author why
 * @since 11/25/23
 */
class TimelinePageAdapter(
    private val onGridItemClickListener: ((AnimeGridImageView.Image) -> Unit)? = null,
) : BaseDifferAdapter<TimelineEntity, RecyclerView.ViewHolder>(IdDiffItemCallback()) {
    private val viewPool by lazy { RecyclerView.RecycledViewPool() }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        item: TimelineEntity?,
    ) {
        item ?: return
        when (val binding = (holder as BaseQuickBindingHolder<*>).binding) {
            is FragmentTimelinePageGridBinding -> {
                onBindGrid(binding, item, onGridItemClickListener)
            }

            is FragmentTimelinePageTextBinding -> {
                onBindText(binding, item)
            }

            is FragmentTimelinePageMediaBinding -> {
                onBindMedia(binding, item)
            }

            else -> throw IllegalArgumentException("error item")
        }
    }

    override fun getItemViewType(position: Int, list: List<TimelineEntity>): Int {
        return list[position].adapterType
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            TimelineAdapterType.TYPE_GRID -> BaseQuickBindingHolder(
                FragmentTimelinePageGridBinding.inflate(context.inflater, parent, false)
                    .apply {
                        imageGrid.setRecycledViewPool(viewPool)
                    }
            )

            TimelineAdapterType.TYPE_TEXT -> BaseQuickBindingHolder(
                FragmentTimelinePageTextBinding.inflate(context.inflater, parent, false)
            )

            TimelineAdapterType.TYPE_MEDIA -> BaseQuickBindingHolder(
                FragmentTimelinePageMediaBinding.inflate(context.inflater, parent, false)
            )

            else -> throw IllegalArgumentException("error type: $viewType")
        }
    }

    companion object {

        private fun onBindGrid(
            binding: FragmentTimelinePageGridBinding,
            item: TimelineEntity,
            onGridItemClickListener: ((AnimeGridImageView.Image) -> Unit)?,
        ) {
            binding.ivAvatar.isInvisible = item.avatar.isBlank()
            binding.ivAvatar.loadImageAnimate(item.avatar)
            binding.tvAction.text = item.title
            binding.tvContent.text = item.content
            binding.tvContent.isVisible = item.content.isNotBlank()
            binding.tvTime.text = item.time

            binding.imageGrid.images = item.gridCard
            binding.imageGrid.onGridItemClickListener = onGridItemClickListener
        }

        private fun onBindMedia(binding: FragmentTimelinePageMediaBinding, item: TimelineEntity) {
            binding.ivAvatar.isInvisible = item.avatar.isBlank()
            binding.ivAvatar.loadImageAnimate(item.avatar)
            binding.tvAction.text = item.title
            binding.tvContent.text = item.content
            binding.tvContent.isVisible = item.content.isNotBlank()
            binding.tvTime.text = item.time

            binding.ivMediaScore.rating = item.mediaCard.score / 2f
            binding.ivMediaScore.isVisible = item.mediaCard.score != 0f
            binding.tvMediaName.text = item.mediaCard.title
            binding.tvMediaDesc.text = item.mediaCard.info
            binding.ivMediaCover.loadImageAnimate(item.mediaCard.cover)
        }

        internal fun onBindText(
            binding: FragmentTimelinePageTextBinding,
            item: TimelineEntity,
            limitLine: Boolean = true,
        ) {
            if (limitLine.not()) {
                binding.tvContent.ellipsize = null
                binding.tvContent.maxLines = Int.MAX_VALUE
            }

            binding.ivAvatar.isInvisible = item.avatar.isBlank()
            binding.ivAvatar.loadImageAnimate(item.avatar)
            binding.tvAction.text = item.title
            binding.tvContent.text = item.content
            binding.tvContent.isVisible = item.content.isNotBlank()
            binding.tvTime.text = item.time
            binding.tvReply.isVisible = item.isSpitOut

            binding.tvReply.text = if (item.commentCount == 0) "回复"
            else String.format("回复：%d", item.commentCount)
        }
    }
}