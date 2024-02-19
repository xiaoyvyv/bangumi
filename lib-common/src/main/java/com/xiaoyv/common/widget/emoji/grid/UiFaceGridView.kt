package com.xiaoyv.common.widget.emoji.grid

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import androidx.annotation.Keep
import androidx.core.view.setPadding
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.xiaoyv.common.R
import com.xiaoyv.common.databinding.ViewEmotionPageItemBinding
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.helper.callback.IdEntity
import com.xiaoyv.common.widget.scroll.AnimeRecyclerView
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.getDpx
import kotlinx.parcelize.Parcelize

/**
 * Class: [UiFaceGridView]
 *
 * @author why
 * @since 12/28/23
 */
class UiFaceGridView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : AnimeRecyclerView(context, attrs) {

    private val itemAdapter by lazy { ItemAdapter() }

    init {
        setHasFixedSize(true)
        layoutManager = GridLayoutManager(context, 8, GridLayoutManager.VERTICAL, false)
        adapter = itemAdapter
        setPadding(getDpx(8f))
    }

    fun fillEmojis(
        map: Map<String, Int>,
        listener: BaseQuickAdapter.OnItemChildClickListener<EmojiItem>?,
    ) {
        itemAdapter.submitList(map.map { EmojiItem(it.key, it.value) })

        listener?.let {
            itemAdapter.addOnItemChildClickListener(R.id.item_emoji, listener = it)
        }
    }

    private class ItemAdapter :
        BaseQuickDiffBindingAdapter<EmojiItem, ViewEmotionPageItemBinding>(IdDiffItemCallback()) {
        override fun BaseQuickBindingHolder<ViewEmotionPageItemBinding>.converted(item: EmojiItem) {
            binding.emoji0.setImageResource(item.resId)
        }
    }

    @Keep
    @Parcelize
    data class EmojiItem(
        var title: String,
        var resId: Int,
    ) : IdEntity, Parcelable {
        override var id: String
            get() = title
            set(value) {
                title = value
            }
    }
}