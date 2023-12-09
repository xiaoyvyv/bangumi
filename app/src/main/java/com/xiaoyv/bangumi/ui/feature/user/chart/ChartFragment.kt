package com.xiaoyv.bangumi.ui.feature.user.chart

import android.animation.ObjectAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.databinding.FragmentUserChartBinding
import com.xiaoyv.bangumi.ui.feature.user.UserViewModel
import com.xiaoyv.blueprint.base.binding.BaseBindingFragment
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
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
    }

    override fun initData() {

    }

    override fun initListener() {
    }

    override fun LifecycleOwner.initViewObserver() {
        activityViewModel.onUserInfoLiveData.observe(this) {
            val entity = it?.chart ?: return@observe
            val ratingDetail: List<MediaDetailEntity.RatingItem> = entity.ratingDetail
            val total = ratingDetail.sumOf { item -> item.count.toLong() }

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
            binding.clLine.setData(ratingDetail, total)
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

    companion object {
        fun newInstance(): ChartFragment {
            return ChartFragment()
        }
    }
}