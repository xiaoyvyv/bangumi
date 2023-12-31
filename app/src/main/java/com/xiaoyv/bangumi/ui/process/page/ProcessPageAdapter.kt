package com.xiaoyv.bangumi.ui.process.page

import android.content.Context
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.xiaoyv.bangumi.databinding.FragmentProcessItemBinding
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.api.response.api.ApiUserEpEntity
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [ProcessPageAdapter]
 *
 * @author why
 * @since 12/24/23
 */
class ProcessPageAdapter(
    private val mediaType: String,
    private val clickItemListener: OnItemChildClickListener<ApiUserEpEntity>,
    private val clickAddEpProgress: (MediaDetailEntity, Boolean) -> Unit,
) : BaseQuickDiffBindingAdapter<MediaDetailEntity, FragmentProcessItemBinding>(IdDiffItemCallback()) {

    private val gridViewPool by lazy { RecycledViewPool() }

    override fun BaseQuickBindingHolder<FragmentProcessItemBinding>.converted(item: MediaDetailEntity) {
        binding.ivCover.loadImageAnimate(item.cover)
        binding.tvTime.text = buildString {
            append(item.rating.ratingCount)
            append(" 人在")
            append(MediaType.action(mediaType))
        }
        binding.tvTitle.text = item.titleCn.ifBlank { item.titleNative }
        binding.tvWeek.text = item.chineseWeek

        // 总进度绑定
        binding.pb1.bind(item, true, clickAddEpProgress)
        binding.pb2.bind(item, false, clickAddEpProgress)

        if (mediaType != MediaType.TYPE_BOOK) {
            binding.epGrid.fillProcessChapters(item.epList.orEmpty(), clickItemListener)
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
    ): BaseQuickBindingHolder<FragmentProcessItemBinding> {
        return super.onCreateViewHolder(context, parent, viewType).apply {
            binding.epGrid.isVisible = mediaType != MediaType.TYPE_BOOK
            binding.epGrid.hasFixedSize()
            binding.epGrid.setRecycledViewPool(gridViewPool)
        }
    }
}