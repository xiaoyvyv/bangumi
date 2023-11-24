package com.xiaoyv.bangumi.ui.home

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.chad.library.adapter.base.BaseMultiItemAdapter.OnItemViewTypeListener
import com.xiaoyv.common.api.parser.entity.HomeImageCardEntity
import com.xiaoyv.common.widget.card.HomeCardView

/**
 * Class: [HomeAdapter]
 *
 * @author why
 * @since 11/25/23
 */
class HomeAdapter : BaseMultiItemAdapter<Any>() {
    class HomeImageEntityViewHolder(val view: HomeCardView) : RecyclerView.ViewHolder(view)

    init {
        addItemType(
            TYPE_CARD_IMAGE,
            object : OnMultiItemAdapterListener<Any, HomeImageEntityViewHolder> {
                override fun onBind(holder: HomeImageEntityViewHolder, position: Int, item: Any?) {
                    holder.view.data = item as? HomeImageCardEntity
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
        ).onItemViewType(OnItemViewTypeListener { position, list ->
            return@OnItemViewTypeListener when (list[position]) {
                is HomeImageCardEntity -> TYPE_CARD_IMAGE
                else -> 1
            }
        })
    }

    companion object {
        const val TYPE_CARD_IMAGE = 1
    }
}