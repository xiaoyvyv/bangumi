package com.xiaoyv.common.widget.star

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.databinding.ViewCommentChartBinding
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.widget.kts.useNotNull
import kotlin.math.roundToInt

/**
 * Class: [AnimeLineChartViw]
 *
 * @author why
 * @since 12/9/23
 */
class AnimeLineChartViw @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val binding = ViewCommentChartBinding.inflate(context.inflater, this)

    fun setData(data: List<MediaDetailEntity.RatingItem>, total: Long, anime: Boolean = true) {
        listOf(
            binding.pb10,
            binding.pb9,
            binding.pb8,
            binding.pb7,
            binding.pb6,
            binding.pb5,
            binding.pb4,
            binding.pb3,
            binding.pb2,
            binding.pb1
        ).forEachIndexed { index, progressBar ->
            useNotNull(data.getOrNull(index)) {
                showAnimeProgress(progressBar, this, total, anime)
            }
        }
    }

    private fun showAnimeProgress(
        bar: ProgressBar,
        ratingItem: MediaDetailEntity.RatingItem,
        totalCount: Long,
        anime: Boolean
    ) {
        if (totalCount == 0L) return
        bar.max = 10000
        if (anime) {
            ObjectAnimator.ofFloat(0f, ratingItem.count / totalCount.toFloat())
                .apply {
                    duration = 2000
                    repeatCount = 0
                    interpolator = AccelerateDecelerateInterpolator()
                    addUpdateListener {
                        bar.progress = (bar.max * (it.animatedValue as Float)).roundToInt()
                    }
                }
                .start()
        } else {
            bar.progress = (ratingItem.count / totalCount.toFloat() * bar.max).roundToInt()
        }
    }
}