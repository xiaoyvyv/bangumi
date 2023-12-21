package com.xiaoyv.common.widget.state

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.databinding.ViewStateBinding
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.widget.stateview.StateViewLiveData
import kotlinx.coroutines.delay

/**
 * Class: [AnimeStateView]
 *
 * @author why
 * @since 12/5/23
 */
class AnimeStateView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {
    private val binding = ViewStateBinding.inflate(context.inflater, this)

    init {
        isVisible = false
    }

    fun showLoading(bias: Float = 0.5f) {
        isVisible = true
        binding.tvTip.isVisible = false
        binding.ivEmptyImage.isVisible = false
        binding.pbProgress.isVisible = true
        binding.pbProgress.updateLayoutParams<LayoutParams> {
            verticalBias = bias
        }
    }

    fun showContent() {
        isVisible = false
        binding.tvTip.isVisible = false
        binding.ivEmptyImage.isVisible = false
        binding.pbProgress.isVisible = false
    }

    fun showTip(@DrawableRes image: Int = 0, message: String? = null) {
        isVisible = true
        binding.pbProgress.isVisible = false
        binding.tvTip.isVisible = true
        binding.ivEmptyImage.isVisible = true
        if (image != 0) {
            binding.ivEmptyImage.setImageResource(image)
        }
        if (!message.isNullOrBlank()) {
            binding.tvTip.text = message
        }
    }

    inline fun initObserver(
        lifecycleOwner: LifecycleOwner,
        loadingViewState: StateViewLiveData,
        loadingBias: Float = 0.5f,
        showContentDelay: Long? = null,
        crossinline canShowLoading: () -> Boolean = { true },
        crossinline canShowTip: () -> Boolean = { true },
        crossinline canShowContent: () -> Boolean = { true },
    ) {
        loadingViewState.observe(lifecycleOwner) {
            when (it.type) {
                StateViewLiveData.StateType.STATE_LOADING -> {
                    if (canShowLoading()) showLoading(loadingBias)
                }

                StateViewLiveData.StateType.STATE_TIPS -> {
                    if (canShowTip()) showTip(it.tipImage, it.tipMsg)
                }

                StateViewLiveData.StateType.STATE_HIDE -> {
                    if (showContentDelay == null) {
                        if (canShowContent()) showContent()
                    } else {
                        lifecycleOwner.launchUI {
                            delay(showContentDelay)
                            if (canShowContent()) showContent()
                        }
                    }
                }
            }
        }
    }
}