package com.xiaoyv.bangumi.ui.discover.home.binder

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.common.api.parser.entity.BgmMediaEntity
import com.xiaoyv.common.api.parser.entity.HomeIndexCardEntity
import com.xiaoyv.common.helper.callback.RecyclerItemTouchedListener
import com.xiaoyv.common.widget.card.HomeCardView

/**
 * 主页底部大图模块
 */
class HomeCardBinder(
    private val imageCardViewPool: RecyclerView.RecycledViewPool,
    private val touchedListener: RecyclerItemTouchedListener,
    private val onItemClick: (BgmMediaEntity) -> Unit,
) : BaseMultiItemAdapter.OnMultiItemAdapterListener<Any, HomeCardBinder.HomeImageEntityViewHolder> {

    override fun onBind(
        holder: HomeImageEntityViewHolder,
        position: Int,
        item: Any?
    ) {
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
        cardView.setItemTouchedListener(touchedListener)
        cardView.setRecycledViewPool(imageCardViewPool)
        return HomeImageEntityViewHolder(cardView)
    }

    class HomeImageEntityViewHolder(val view: HomeCardView) : RecyclerView.ViewHolder(view)
}


