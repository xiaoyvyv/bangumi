package com.xiaoyv.common.widget.emoji.format

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.setPadding
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.xiaoyv.common.databinding.ViewFormatItemBinding
import com.xiaoyv.common.helper.BBCodeHelper
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.CommonId
import com.xiaoyv.common.widget.emoji.grid.UiFaceGridView
import com.xiaoyv.common.widget.scroll.AnimeRecyclerView
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.getDpx

/**
 * Class: [FormatGridView]
 *
 * @author why
 * @since 1/1/24
 */
class FormatGridView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : AnimeRecyclerView(context, attrs) {

    private val itemAdapter by lazy { ItemAdapter() }

    var listener: BaseQuickAdapter.OnItemChildClickListener<BBCodeHelper.BBCode>? = null
        set(value) {
            field = value
            if (value != null) {
                itemAdapter.addOnItemChildClickListener(CommonId.menu, value)
            }
        }

    init {
        hasFixedSize()
        layoutManager = GridLayoutManager(context, 6, GridLayoutManager.VERTICAL, false)
        adapter = itemAdapter
        setPadding(getDpx(8f))

        itemAdapter.submitList(BBCodeHelper.bbCodes)
    }

    fun setON(
        listener: BaseQuickAdapter.OnItemChildClickListener<UiFaceGridView.EmojiItem>?,
    ) {

    }

    private class ItemAdapter :
        BaseQuickDiffBindingAdapter<BBCodeHelper.BBCode, ViewFormatItemBinding>(IdDiffItemCallback()) {

        override fun BaseQuickBindingHolder<ViewFormatItemBinding>.converted(item: BBCodeHelper.BBCode) {
            binding.menu.setImageResource(item.icon)
        }
    }
}