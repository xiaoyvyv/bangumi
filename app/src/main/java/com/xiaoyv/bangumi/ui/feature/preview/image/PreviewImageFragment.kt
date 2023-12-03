package com.xiaoyv.bangumi.ui.feature.preview.image

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.Target
import com.xiaoyv.bangumi.databinding.ActivityPreviewPageBinding
import com.xiaoyv.blueprint.base.binding.BaseBindingFragment
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.config.glide.ProgressTarget
import com.xiaoyv.common.kts.debugLog
import kotlin.math.roundToInt


/**
 * Class: [PreviewImageFragment]
 *
 * @author why
 * @since 12/1/23
 */
class PreviewImageFragment : BaseBindingFragment<ActivityPreviewPageBinding>() {
    private var imageUrl: String = ""
    private var position: Int = 0
    private var total = 0

    private val parentActivity: PreviewImageActivity
        get() = requireActivity() as PreviewImageActivity

    override fun initArgumentsData(arguments: Bundle) {
        imageUrl = arguments.getString(NavKey.KEY_STRING).orEmpty()
        position = arguments.getInt(NavKey.KEY_INTEGER)
        total = arguments.getInt(NavKey.KEY_INTEGER_SECOND)
    }

    override fun initView() {
    }

    override fun initData() {
        val target = ImageProgressTarget(
            imageUrl, requireContext(),
            BitmapImageViewTarget(binding.ivImage)
        )

        Glide.with(requireContext())
            .asBitmap()
            .load(imageUrl)
            .into(target)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initListener() {
        binding.ivImage.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (binding.ivImage.isZoomed) {
                        parentActivity.setVpEnable(false)
                    }
                }

                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    parentActivity.setVpEnable(true)
                }
            }
            false
        }
    }

    inner class ImageProgressTarget<Z>(model: String?, context: Context, target: Target<Z>) :
        ProgressTarget<String, Z>(model, context, target) {

        override fun onConnecting() {
            debugLog { "ImageProgressTarget: onConnecting..." }
            binding.gpProgress.isVisible = true
            binding.tvProgress.text = "0%"
            binding.pbProgress.progress = 0
        }

        override fun onDownloading(bytesRead: Long, expectedLength: Long) {
            if (expectedLength != 0L) {
                val progress = ((bytesRead / expectedLength.toDouble()) * 100).roundToInt()
                binding.pbProgress.progress = progress
                binding.tvProgress.text = String.format("%d%%", progress)
            }
            debugLog { "ImageProgressTarget: bytesRead: $bytesRead / expectedLength: $$expectedLength" }
        }

        override fun onDownloaded() {
            debugLog { "ImageProgressTarget: onDownloaded" }
        }

        override fun onDelivered() {
            debugLog { "ImageProgressTarget: onDelivered" }
            binding.tvProgress.text = "100%"
            binding.pbProgress.progress = 100
            binding.gpProgress.isVisible = false
        }
    }

    companion object {
        fun newInstance(imageUrl: String, position: Int, total: Int): PreviewImageFragment {
            return PreviewImageFragment().apply {
                arguments = bundleOf(
                    NavKey.KEY_STRING to imageUrl,
                    NavKey.KEY_INTEGER to position,
                    NavKey.KEY_INTEGER_SECOND to total
                )
            }
        }
    }
}