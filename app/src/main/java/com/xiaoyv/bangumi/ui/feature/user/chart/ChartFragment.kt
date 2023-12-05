package com.xiaoyv.bangumi.ui.feature.user.chart

import android.animation.ObjectAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ProgressBar
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.databinding.FragmentUserChartBinding
import com.xiaoyv.bangumi.ui.feature.user.UserViewModel
import com.xiaoyv.blueprint.base.binding.BaseBindingFragment
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.widget.kts.useNotNull
import kotlin.math.roundToInt

/**
 * Class: [ChartFragment]
 *
 * @author why
 * @since 12/3/23
 */
class ChartFragment : BaseBindingFragment<FragmentUserChartBinding>() {
    private val activityViewModel: UserViewModel by activityViewModels()

    override fun initView() {
        binding.stateView.showLoading(0.2f)

        binding.clLine.doOnPreDraw {
            listOf(
                binding.pb1,
                binding.pb2,
                binding.pb3,
                binding.pb4,
                binding.pb5,
                binding.pb6,
                binding.pb7,
                binding.pb8,
                binding.pb9,
                binding.pb10
            )
        }
    }

    override fun initData() {

    }

    override fun initListener() {
    }

    override fun LifecycleOwner.initViewObserver() {
        activityViewModel.onUserInfoLiveData.observe(this) {
            val entity = it?.chart ?: return@observe
            val total = entity.ratingDetail.sumOf { item -> item.count }
            animeValue(entity.saveCount) { value ->
                binding.tvName1.text = value.roundToInt().toString()
            }
            animeValue(entity.finishCount) { value ->
                binding.tvName2.text = value.roundToInt().toString()
            }
            animeValue(entity.finishRate) { value ->
                binding.tvName3.text = String.format("%.1f%%", value)
            }
            animeValue(entity.averageScore) { value ->
                binding.tvName4.text = String.format("%.1f", value)
            }
            animeValue(entity.standardDeviation) { value ->
                binding.tvName5.text = String.format("%.1f", value)
            }
            animeValue(entity.ratingCount) { value ->
                binding.tvName6.text = value.roundToInt().toString()
            }

            binding.tvCount.text = String.format("总评数：%d", total)

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
                useNotNull(entity.ratingDetail.getOrNull(index)) {
                    showAnimeProgress(progressBar, this, total)
                }
            }

            binding.stateView.showContent()
        }
    }

    private fun animeValue(value: Any, function: (Float) -> Unit) {
        val targetValue = value.toString().toFloatOrNull() ?: 0f
        ObjectAnimator.ofFloat(0f, targetValue)
            .apply {
                repeatCount = 0
                duration = 2000
                interpolator = AccelerateDecelerateInterpolator()
                addUpdateListener {
                    function(it.animatedValue as Float)
                }
            }
            .start()
    }

    private fun showAnimeProgress(
        bar: ProgressBar,
        ratingItem: MediaDetailEntity.RatingItem,
        totalCount: Int
    ) {
        if (totalCount == 0) return
        bar.max = 10000
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
    }

    companion object {
        fun newInstance(): ChartFragment {
            return ChartFragment()
        }
    }
}