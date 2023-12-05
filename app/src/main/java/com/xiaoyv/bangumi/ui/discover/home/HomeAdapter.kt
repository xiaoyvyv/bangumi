package com.xiaoyv.bangumi.ui.discover.home

import android.content.Context
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.chad.library.adapter.base.BaseMultiItemAdapter.OnItemViewTypeListener
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentHomeBannerBinding
import com.xiaoyv.bangumi.databinding.FragmentHomeCalendarBinding
import com.xiaoyv.common.api.parser.entity.BgmMediaEntity
import com.xiaoyv.common.api.parser.entity.HomeIndexBannerEntity
import com.xiaoyv.common.api.parser.entity.HomeIndexCalendarEntity
import com.xiaoyv.common.api.parser.entity.HomeIndexCardEntity
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.widget.card.HomeCardView
import com.xiaoyv.widget.binder.BaseQuickBindingHolder

/**
 * Class: [HomeAdapter]
 *
 * @author why
 * @since 11/25/23
 */
class HomeAdapter(onItemClick: (BgmMediaEntity) -> Unit) : BaseMultiItemAdapter<Any>() {

    init {
        this.addItemType(TYPE_TOP_BANNER, HomeBannerBinder())
            .addItemType(TYPE_CALENDAR_PREVIEW, HomeCalendarImageBinder(onItemClick))
            .addItemType(TYPE_MEDIA_CARD, HomeCardImageBinder(onItemClick))
            .onItemViewType(OnItemViewTypeListener { position, list ->
                return@OnItemViewTypeListener when (list[position]) {
                    is HomeIndexCardEntity -> TYPE_MEDIA_CARD
                    is HomeIndexCalendarEntity -> TYPE_CALENDAR_PREVIEW
                    is HomeIndexBannerEntity -> TYPE_TOP_BANNER
                    else -> 1
                }
            })
    }

    /**
     * 顶部 Banner 模块
     */
    class HomeBannerBinder :
        OnMultiItemAdapterListener<Any, BaseQuickBindingHolder<FragmentHomeBannerBinding>> {

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
            debugLog { "顶部 Banner 模块:$position" }
            (item as? HomeIndexBannerEntity)?.apply {
                val binding = holder.binding
                val context = binding.root.context
                val imageViews = banners.map {
                    AppCompatImageView(context, null).apply {
                        layoutParams = bannerItemParams
                        loadImageAnimate(it)
                    }
                }
                binding.banner.setData(imageViews)
            }
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
    }

    /**
     * 今日放送 | 明日放送模块
     */
    class HomeCalendarImageBinder(private val onItemClick: (BgmMediaEntity) -> Unit) :
        OnMultiItemAdapterListener<Any, BaseQuickBindingHolder<FragmentHomeCalendarBinding>> {
        private val itemTodayAdapter by lazy { HomeCardView.ItemAdapter() }
        private val itemTomorrowAdapter by lazy { HomeCardView.ItemAdapter() }

        override fun onBind(
            holder: BaseQuickBindingHolder<FragmentHomeCalendarBinding>,
            position: Int,
            item: Any?
        ) {
            debugLog { "今日放送 | 明日放送模块 模块:$position" }
            val calendarEntity = item as? HomeIndexCalendarEntity
            val binding = holder.binding

            binding.tvTodayDesc.text = calendarEntity?.tip

            binding.rvToday.setHasFixedSize(true)
            binding.rvToday.adapter = itemTodayAdapter

            binding.rvTomorrow.setHasFixedSize(true)
            binding.rvTomorrow.adapter = itemTomorrowAdapter

            itemTodayAdapter.submitList(calendarEntity?.today.orEmpty())
            itemTomorrowAdapter.submitList(calendarEntity?.tomorrow.orEmpty())

            itemTodayAdapter.setOnDebouncedChildClickListener(
                R.id.item_root,
                block = onItemClick
            )

            itemTomorrowAdapter.setOnDebouncedChildClickListener(
                R.id.item_root,
                block = onItemClick
            )
        }

        override fun onCreate(
            context: Context,
            parent: ViewGroup,
            viewType: Int
        ): BaseQuickBindingHolder<FragmentHomeCalendarBinding> {
            return BaseQuickBindingHolder(
                FragmentHomeCalendarBinding.inflate(context.inflater, parent, false)
            )
        }
    }

    /**
     * 主页底部大图模块
     */
    class HomeCardImageBinder(private val onItemClick: (BgmMediaEntity) -> Unit) :
        OnMultiItemAdapterListener<Any, HomeImageEntityViewHolder> {
        override fun onBind(
            holder: HomeImageEntityViewHolder,
            position: Int,
            item: Any?
        ) {
            debugLog { "主页底部大图模块 模块:$position" }
            holder.view.data = item as? HomeIndexCardEntity
            holder.view.onItemClick = onItemClick
        }

        override fun onCreate(
            context: Context,
            parent: ViewGroup,
            viewType: Int
        ): HomeImageEntityViewHolder {
            val cardView = HomeCardView(context).apply {
                layoutParams = RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            return HomeImageEntityViewHolder(cardView)
        }
    }

    class HomeImageEntityViewHolder(val view: HomeCardView) : RecyclerView.ViewHolder(view)

    companion object {
        const val TYPE_TOP_BANNER = 0
        const val TYPE_CALENDAR_PREVIEW = 1
        const val TYPE_MEDIA_CARD = 2
    }
}