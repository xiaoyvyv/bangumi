package com.xiaoyv.common.widget.image

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.R
import com.xiaoyv.common.databinding.ViewImageGridBinding
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.widget.decoration.GridItemDecoration
import com.xiaoyv.widget.binder.BaseQuickBindingAdapter
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.kts.getDpx

/**
 * Class: [AnimeGridImageView]
 *
 * @author why
 * @since 11/25/23
 */
class AnimeGridImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {
    private val imageAdapter by lazy { GridAdapter() }

    var images: List<Image> = emptyList()
        set(value) {
            field = value
            imageAdapter.submitList(value)
        }

    /**
     * 点击事件
     */
    var onGridItemClickListener: ((Image) -> Unit)? = null
        set(value) {
            field = value
            if (value != null) {
                imageAdapter.setOnDebouncedChildClickListener(R.id.iv_image, block = value)
            }
        }

    init {
        hasFixedSize()
        itemAnimator = null
        overScrollMode = OVER_SCROLL_NEVER
        isNestedScrollingEnabled = false
        layoutManager = GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false)
        addItemDecoration(
            GridItemDecoration.Builder(context)
                .setColor(Color.TRANSPARENT)
                .setHorizontalSpan(getDpx(4f))
                .setVerticalSpan(getDpx(4f))
                .build()
        )
        adapter = imageAdapter
    }

    interface Image {
        val image: String
    }

    class GridAdapter : BaseQuickBindingAdapter<Image, ViewImageGridBinding>() {
        override fun BaseQuickBindingHolder<ViewImageGridBinding>.converted(item: Image) {
            binding.ivImage.loadImageAnimate(item.image)
        }
    }
}