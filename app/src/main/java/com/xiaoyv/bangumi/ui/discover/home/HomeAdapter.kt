package com.xiaoyv.bangumi.ui.discover.home

import android.content.Context
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.chad.library.adapter.base.BaseMultiItemAdapter.OnItemViewTypeListener
import com.xiaoyv.bangumi.databinding.FragmentHomeBannerBinding
import com.xiaoyv.bangumi.databinding.FragmentHomeCalendarBinding
import com.xiaoyv.common.api.parser.entity.HomeIndexBannerEntity
import com.xiaoyv.common.api.parser.entity.HomeIndexCalendarEntity
import com.xiaoyv.common.api.parser.entity.HomeIndexCardEntity
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.widget.card.HomeCardView
import com.xiaoyv.widget.binder.BaseQuickBindingHolder

/**
 * Class: [HomeAdapter]
 *
 * @author why
 * @since 11/25/23
 */
class HomeAdapter : BaseMultiItemAdapter<Any>() {

    init {
        this.addItemType(TYPE_TOP_BANNER, HomeBannerBinder())
            .addItemType(TYPE_CALENDAR_PREVIEW, HomeCalendarImageBinder())
            .addItemType(TYPE_MEDIA_CARD, HomeCardImageBinder())
            .onItemViewType(OnItemViewTypeListener { position, list ->
                return@OnItemViewTypeListener when (list[position]) {
                    is HomeIndexCardEntity -> TYPE_MEDIA_CARD
                    is HomeIndexCalendarEntity -> TYPE_CALENDAR_PREVIEW
                    is HomeIndexBannerEntity -> TYPE_TOP_BANNER
                    else -> 1
                }
            })
    }

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

    class HomeCalendarImageBinder :
        OnMultiItemAdapterListener<Any, BaseQuickBindingHolder<FragmentHomeCalendarBinding>> {
        private val itemTodayAdapter by lazy { HomeCardView.ItemAdapter() }
        private val itemTomorrowAdapter by lazy { HomeCardView.ItemAdapter() }

        override fun onBind(
            holder: BaseQuickBindingHolder<FragmentHomeCalendarBinding>,
            position: Int,
            item: Any?
        ) {
            val calendarEntity = item as? HomeIndexCalendarEntity
            val binding = holder.binding

            binding.tvTodayDesc.text = calendarEntity?.tip

            binding.rvToday.setHasFixedSize(true)
            binding.rvToday.adapter = itemTodayAdapter

            binding.rvTomorrow.setHasFixedSize(true)
            binding.rvTomorrow.adapter = itemTomorrowAdapter

            itemTodayAdapter.submitList(calendarEntity?.today.orEmpty())
            itemTomorrowAdapter.submitList(calendarEntity?.tomorrow.orEmpty())
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

    class HomeCardImageBinder : OnMultiItemAdapterListener<Any, HomeImageEntityViewHolder> {
        override fun onBind(
            holder: HomeImageEntityViewHolder,
            position: Int,
            item: Any?
        ) {
            holder.view.data = item as? HomeIndexCardEntity
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