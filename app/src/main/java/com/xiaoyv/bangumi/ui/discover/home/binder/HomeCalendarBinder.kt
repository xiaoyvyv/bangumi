package com.xiaoyv.bangumi.ui.discover.home.binder

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentHomeCalendarBinding
import com.xiaoyv.common.api.parser.entity.BgmMediaEntity
import com.xiaoyv.common.api.parser.entity.HomeIndexCalendarEntity
import com.xiaoyv.common.helper.callback.RecyclerItemTouchedListener
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.widget.card.HomeCardView
import com.xiaoyv.widget.binder.BaseQuickBindingHolder

/**
 * 今日放送 | 明日放送模块
 */
class HomeCalendarBinder(
    private val touchedListener: RecyclerItemTouchedListener,
    private val onItemClick: (BgmMediaEntity) -> Unit,
) : BaseMultiItemAdapter.OnMultiItemAdapterListener<Any, BaseQuickBindingHolder<FragmentHomeCalendarBinding>> {
    private val viewPool by lazy { RecycledViewPool() }

    private val itemTodayAdapter by lazy {
        HomeCardView.ItemAdapter().apply {
            setOnDebouncedChildClickListener(R.id.item_root, block = onItemClick)
        }
    }
    private val itemTomorrowAdapter by lazy {
        HomeCardView.ItemAdapter().apply {
            setOnDebouncedChildClickListener(R.id.item_root, block = onItemClick)
        }
    }

    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentHomeCalendarBinding>,
        position: Int,
        item: Any?,
    ) {
        val calendarEntity = item as? HomeIndexCalendarEntity
        val binding = holder.binding

        binding.tvTodayDesc.text = calendarEntity?.tip

        binding.rvToday.setHasFixedSize(true)
        binding.rvToday.adapter = itemTodayAdapter
        binding.rvToday.addOnItemTouchListener(touchedListener)

        binding.rvTomorrow.setHasFixedSize(true)
        binding.rvTomorrow.adapter = itemTomorrowAdapter
        binding.rvTomorrow.addOnItemTouchListener(touchedListener)

        itemTodayAdapter.submitList(calendarEntity?.today.orEmpty())
        itemTomorrowAdapter.submitList(calendarEntity?.tomorrow.orEmpty())
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
    ): BaseQuickBindingHolder<FragmentHomeCalendarBinding> {
        val binding = FragmentHomeCalendarBinding.inflate(context.inflater, parent, false)
        binding.rvToday.setRecycledViewPool(viewPool)
        binding.rvTomorrow.setRecycledViewPool(viewPool)
        return BaseQuickBindingHolder(binding)
    }
}