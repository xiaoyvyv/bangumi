package com.xiaoyv.bangumi.ui.discover.home.binder

import android.content.Context
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentHomeBannerBinding
import com.xiaoyv.bangumi.databinding.FragmentHomeBannerFeatureBinding
import com.xiaoyv.common.api.parser.entity.HomeIndexBannerEntity
import com.xiaoyv.common.config.bean.HomeIndexFeature
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * 顶部 Banner 模块
 */
class HomeBannerBinder(private val onClickFeature: (HomeIndexFeature) -> Unit) :
    BaseMultiItemAdapter.OnMultiItemAdapterListener<Any, BaseQuickBindingHolder<FragmentHomeBannerBinding>> {

    private val itemAdapter by lazy {
        ItemAdapter().apply {
            setOnDebouncedChildClickListener(R.id.item_feature, block = onClickFeature)
        }
    }

    private val bannerItemParams by lazy {
        RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
    }

    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentHomeBannerBinding>,
        position: Int,
        item: Any?
    ) {
        if (item !is HomeIndexBannerEntity) return
        holder.binding.banner.setData(item.banners.map {
            AppCompatImageView(holder.binding.root.context, null).apply {
                layoutParams = bannerItemParams
                loadImageAnimate(it)
            }
        })
        holder.binding.rvEnter.adapter = itemAdapter
        itemAdapter.submitList(item.features)
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): BaseQuickBindingHolder<FragmentHomeBannerBinding> {
        return BaseQuickBindingHolder(
            FragmentHomeBannerBinding.inflate(context.inflater, parent, false)
        )
    }

    private class ItemAdapter : BaseQuickDiffBindingAdapter<HomeIndexFeature,
            FragmentHomeBannerFeatureBinding>(IdDiffItemCallback()) {
        override fun BaseQuickBindingHolder<FragmentHomeBannerFeatureBinding>.converted(item: HomeIndexFeature) {
            binding.ivIcon.setImageResource(item.icon)
            binding.tvTitle.text = item.title
        }
    }
}