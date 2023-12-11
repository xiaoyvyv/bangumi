package com.xiaoyv.bangumi.ui.media.detail.overview.binder

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentOverviewPreviewBinding
import com.xiaoyv.bangumi.databinding.FragmentOverviewPreviewItemBinding
import com.xiaoyv.bangumi.ui.media.detail.overview.OverviewAdapter
import com.xiaoyv.common.api.response.douban.DouBanPhotoEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.helper.callback.RecyclerItemTouchedListener
import com.xiaoyv.common.kts.forceCast
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [OverviewPreviewBinder]
 *
 * @author why
 * @since 11/30/23
 */
class OverviewPreviewBinder(
    private val touchedListener: RecyclerItemTouchedListener,
    private val clickItemListener: (DouBanPhotoEntity.Photo) -> Unit
) : BaseMultiItemAdapter.OnMultiItemAdapterListener<OverviewAdapter.Item, BaseQuickBindingHolder<FragmentOverviewPreviewBinding>> {

    private val itemAdapter by lazy {
        ItemAdapter().apply {
            setOnDebouncedChildClickListener(R.id.iv_preview, block = clickItemListener)
        }
    }

    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentOverviewPreviewBinding>,
        position: Int,
        item: OverviewAdapter.Item?
    ) {
        item ?: return
        holder.binding.tvSectionPreview.title = item.title
        item.entity.forceCast<List<DouBanPhotoEntity.Photo>>().apply {
            itemAdapter.submitList(this)
        }
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): BaseQuickBindingHolder<FragmentOverviewPreviewBinding> {
        val binding = FragmentOverviewPreviewBinding.inflate(context.inflater, parent, false)
        binding.rvPreview.adapter = itemAdapter
        binding.rvPreview.addOnItemTouchListener(touchedListener)
        return BaseQuickBindingHolder(binding)
    }

    private class ItemAdapter : BaseQuickDiffBindingAdapter<DouBanPhotoEntity.Photo,
            FragmentOverviewPreviewItemBinding>(IdDiffItemCallback()) {
        override fun BaseQuickBindingHolder<FragmentOverviewPreviewItemBinding>.converted(item: DouBanPhotoEntity.Photo) {
            binding.ivPreview.loadImageAnimate(item.image?.large?.url.orEmpty())
        }
    }
}