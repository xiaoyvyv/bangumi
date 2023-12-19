package com.xiaoyv.bangumi.ui.discover.home.binder

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.PagerSnapHelper
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentHomeBannerBinding
import com.xiaoyv.bangumi.databinding.FragmentHomeBannerFeatureBinding
import com.xiaoyv.bangumi.databinding.FragmentHomeBannerImageBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.common.api.parser.entity.HomeIndexBannerEntity
import com.xiaoyv.common.config.bean.HomeIndexFeature
import com.xiaoyv.common.config.bean.SampleImageEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.helper.callback.RecyclerItemTouchedListener
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * 顶部 Banner 模块
 */
class HomeBannerBinder(
    private val touchedListener: RecyclerItemTouchedListener,
    private val onClickFeature: (HomeIndexFeature) -> Unit,
) : BaseMultiItemAdapter.OnMultiItemAdapterListener<Any, BaseQuickBindingHolder<FragmentHomeBannerBinding>> {

    private val itemAdapter by lazy {
        ItemAdapter().apply {
            setOnDebouncedChildClickListener(R.id.item_feature, block = onClickFeature)
        }
    }

    private val bannerAdapter by lazy {
        BannerAdapter().apply {
            setOnDebouncedChildClickListener(R.id.iv_cover) {
                RouteHelper.jumpMediaDetail(it.id)
            }
        }
    }

    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentHomeBannerBinding>,
        position: Int,
        item: Any?,
    ) {
        if (item !is HomeIndexBannerEntity) return
        holder.binding.rvBanner.adapter = bannerAdapter
        holder.binding.rvEnter.adapter = itemAdapter
        bannerAdapter.submitList(item.banners)
        itemAdapter.submitList(item.features)
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
    ): BaseQuickBindingHolder<FragmentHomeBannerBinding> {
        val binding = FragmentHomeBannerBinding.inflate(context.inflater, parent, false)
        binding.rvBanner.addOnItemTouchListener(touchedListener)
        PagerSnapHelper().attachToRecyclerView(binding.rvBanner)
        return BaseQuickBindingHolder(binding)
    }

    private class ItemAdapter : BaseQuickDiffBindingAdapter<HomeIndexFeature,
            FragmentHomeBannerFeatureBinding>(IdDiffItemCallback()) {
        override fun BaseQuickBindingHolder<FragmentHomeBannerFeatureBinding>.converted(item: HomeIndexFeature) {
            binding.ivIcon.setImageResource(item.icon)
            binding.tvTitle.text = item.title
        }
    }

    private class BannerAdapter : BaseQuickDiffBindingAdapter<SampleImageEntity,
            FragmentHomeBannerImageBinding>(IdDiffItemCallback()) {
        override fun BaseQuickBindingHolder<FragmentHomeBannerImageBinding>.converted(item: SampleImageEntity) {
            binding.ivCover.loadImageAnimate(item.image)
            binding.tvName.text = item.title
        }
    }
}