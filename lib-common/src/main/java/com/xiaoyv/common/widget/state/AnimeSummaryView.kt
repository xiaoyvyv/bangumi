package com.xiaoyv.common.widget.state

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.method.LinkMovementMethodCompat
import androidx.recyclerview.widget.RecyclerView
import com.xiaoyv.common.databinding.ViewSummaryBinding
import com.xiaoyv.common.kts.inflater

/**
 * Class: [AnimeSummaryView]
 *
 * @author why
 * @since 12/6/23
 */
class AnimeSummaryView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {
    private val binding = ViewSummaryBinding.inflate(context.inflater, this)

    val section get() = binding.tvSection

    var summary: CharSequence = ""
        set(value) {
            field = value
            binding.tvSummaryContent.text = value.ifBlank { "暂时没有介绍呢!" }
        }

    var summaries: List<CharSequence> = emptyList()
        set(value) {
            field = value
            if (value.isEmpty()) {
                binding.tvSummaryContent.text = "暂时没有内容呢!"
            } else {
                binding.tvSummaryContent.text = null
                value.forEach {
                    binding.tvSummaryContent.append(it)
                    binding.tvSummaryContent.append("\n")
                }
            }
        }

    class Holder(private val view: AnimeSummaryView) :
        RecyclerView.ViewHolder(view) {

        val summaryView: AnimeSummaryView get() = view
    }

    companion object {
        @JvmStatic
        fun createHolder(context: Context, parent: ViewGroup): Holder {
            return Holder(AnimeSummaryView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            })
        }
    }
}