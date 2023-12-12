package com.xiaoyv.bangumi.ui.timeline.page.binder

import android.content.Context
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.databinding.FragmentTimelinePageGridBinding
import com.xiaoyv.common.api.parser.entity.TimelineEntity
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.widget.image.AnimeGridImageView
import com.xiaoyv.widget.binder.BaseQuickBindingHolder

/**
 * Class: [TimelineGridBinder]
 *
 * @author why
 * @since 12/12/23
 */
class TimelineGridBinder(
    private var onGridItemClickListener: ((AnimeGridImageView.Image) -> Unit)? = null
) : BaseMultiItemAdapter.OnMultiItemAdapterListener<TimelineEntity, BaseQuickBindingHolder<FragmentTimelinePageGridBinding>> {

    private val viewPool by lazy { RecycledViewPool() }

    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentTimelinePageGridBinding>,
        position: Int,
        item: TimelineEntity?
    ) {
        val entity = item ?: return

        holder.binding.ivAvatar.isInvisible = entity.avatar.isBlank()
        holder.binding.ivAvatar.loadImageAnimate(entity.avatar)
        holder.binding.tvAction.text = entity.title
        holder.binding.tvContent.text = entity.content
        holder.binding.tvContent.isVisible = entity.content.isNotBlank()
        holder.binding.tvTime.text = entity.time

        holder.binding.imageGrid.images = entity.gridCard
        holder.binding.imageGrid.onGridItemClickListener = onGridItemClickListener
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): BaseQuickBindingHolder<FragmentTimelinePageGridBinding> {
        val binding = FragmentTimelinePageGridBinding.inflate(context.inflater, parent, false)
        binding.imageGrid.setRecycledViewPool(viewPool)
        return BaseQuickBindingHolder(binding)
    }
}