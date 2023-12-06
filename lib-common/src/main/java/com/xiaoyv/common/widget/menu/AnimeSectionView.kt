package com.xiaoyv.common.widget.menu

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.xiaoyv.common.databinding.ViewSectionBinding
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.widget.callback.setOnFastLimitClickListener

/**
 * Class: [AnimeSectionView]
 *
 * @author why
 * @since 12/6/23
 */
class AnimeSectionView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {
    private val binding = ViewSectionBinding.inflate(context.inflater, this)

    var title: String = ""
        set(value) {
            field = value
            binding.tvItemTitle.text = value
        }

    var more: String = ""
        set(value) {
            field = value
            binding.tvMore.text = value
        }

    var onMoreClickListener: (() -> Unit)? = null

    init {
        binding.tvMore.setOnFastLimitClickListener {
            onMoreClickListener?.invoke()
        }
    }
}