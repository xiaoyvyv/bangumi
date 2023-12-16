package com.xiaoyv.bangumi.ui.feature.preview.image

import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.xiaoyv.bangumi.databinding.ActivityPreviewPageBinding
import com.xiaoyv.blueprint.base.binding.BaseBindingFragment
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.config.glide.ProgressTarget
import com.xiaoyv.common.helper.callback.SubsamplingEventListener
import com.xiaoyv.common.helper.callback.SubsamplingTarget
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.widget.kts.loadImage
import kotlin.math.roundToInt


/**
 * Class: [PreviewImageFragment]
 *
 * TODO:// Crash https://bangumi.tv/blog/327666
 *
 * @author why
 * @since 12/1/23
 */
class PreviewImageFragment : BaseBindingFragment<ActivityPreviewPageBinding>() {
    private var imageUrl: String = ""
    private var position: Int = 0
    private var total = 0

    private val isLoadNormal
        get() = imageUrl.endsWith(".gif", false) || imageUrl.startsWith("file:///android_asset")

    override fun initArgumentsData(arguments: Bundle) {
        imageUrl = arguments.getString(NavKey.KEY_STRING).orEmpty()
        position = arguments.getInt(NavKey.KEY_INTEGER)
        total = arguments.getInt(NavKey.KEY_INTEGER_SECOND)
    }

    override fun initView() {
        binding.ivImage.isQuickScaleEnabled = true
        if (isLoadNormal) {
            binding.ivGif.isVisible = true
            binding.ivImage.isVisible = false
        } else {
            binding.ivGif.isVisible = false
            binding.ivImage.isVisible = true
        }
    }

    override fun initData() {
        if (isLoadNormal) {
            binding.ivGif.loadImage(imageUrl, cropOrFit = false)
            binding.gpProgress.isVisible = false
        } else {
            val target = ImageProgressTarget(
                imageUrl, requireContext(),
                SubsamplingTarget(binding.ivImage)
            )

            Glide.with(requireContext())
                .asFile()
                .load(imageUrl)
                .into(target)
        }
    }

    override fun initListener() {
        binding.ivImage.setOnImageEventListener(object : SubsamplingEventListener() {
            override fun onImageLoaded() {
                val scale = binding.ivImage.scale
                binding.ivImage.minScale = scale
                binding.ivImage.maxScale = scale * 5f
                binding.ivImage.setDoubleTapZoomScale(scale * 2.5f)
                binding.ivImage.resetScaleAndCenter()
            }
        })
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