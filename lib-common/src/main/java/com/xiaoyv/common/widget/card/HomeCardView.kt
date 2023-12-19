package com.xiaoyv.common.widget.card

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.xiaoyv.common.api.parser.entity.BgmMediaEntity
import com.xiaoyv.common.api.parser.entity.HomeIndexCardEntity
import com.xiaoyv.common.databinding.ViewHomeCardBinding
import com.xiaoyv.common.databinding.ViewHomeCardItemBinding
import com.xiaoyv.common.helper.callback.RecyclerItemTouchedListener
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.setOnDebouncedItemClickListener
import com.xiaoyv.widget.binder.BaseQuickBindingAdapter
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.kts.loadImage

/**
 * Class: [HomeCardView]
 *
 * @author why
 * @since 11/24/23
 */
class HomeCardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {
    private val binding = ViewHomeCardBinding.inflate(LayoutInflater.from(context), this)
    private val itemAdapter by lazy { ItemAdapter() }

    var data: HomeIndexCardEntity? = null
        set(value) {
            field = value
            refreshCardImages()
        }

    var onItemClick: (BgmMediaEntity) -> Unit = {}

    init {
        binding.rvSmall.setHasFixedSize(true)
        binding.rvSmall.adapter = itemAdapter

        itemAdapter.setOnDebouncedItemClickListener {
            onItemClick(it)
        }
    }

    fun setRecycledViewPool(viewPool: RecycledViewPool) {
        binding.rvSmall.setRecycledViewPool(viewPool)
    }

    private fun refreshCardImages() {
        val images = data?.images.orEmpty()
        if (images.isEmpty()) return

        // 左侧
        val card1 = images.first()
        binding.cardTitle.title = data?.title.orEmpty()
        binding.cardTitle.more = ""
        binding.mask1.cardBigTitle.text = card1.title
        binding.mask1.cardBigAttention.text = card1.attention
        binding.cardLeft.loadImageAnimate(card1.image)
        binding.cardLeft.setOnFastLimitClickListener {
            onItemClick(card1)
        }

        // 右侧
        val card2 = images.getOrNull(1)
        binding.cardTitle.title = data?.title.orEmpty()
        binding.cardTitle.more = ""
        binding.mask2.cardBigTitle.text = card2?.title
        binding.mask2.cardBigAttention.text = card2?.attention
        binding.cardRight.loadImageAnimate(card2?.image)
        binding.cardRight.setOnFastLimitClickListener {
            card2?.let { entity -> onItemClick(entity) }
        }

        if (images.size > 2) {
            itemAdapter.submitList(images.subList(2, images.size))
        }
    }

    fun setItemTouchedListener(touchedListener: RecyclerItemTouchedListener) {
        binding.rvSmall.addOnItemTouchListener(touchedListener)
    }

    class ItemAdapter :
        BaseQuickBindingAdapter<BgmMediaEntity, ViewHomeCardItemBinding>() {
        override fun BaseQuickBindingHolder<ViewHomeCardItemBinding>.converted(item: BgmMediaEntity) {
            binding.cardSmall.loadImage(item.image)
            binding.cardSmallTitle.text = item.title
            binding.cardSmallAttention.text = item.attention
        }
    }
}