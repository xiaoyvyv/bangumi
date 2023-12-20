package com.xiaoyv.bangumi.ui.feature.preview.image.page

import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xiaoyv.bangumi.databinding.ActivityPreviewPageBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.config.glide.ProgressTarget
import com.xiaoyv.common.helper.callback.SubsamplingEventListener
import com.xiaoyv.common.helper.callback.SubsamplingTarget
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.widget.kts.loadImage
import kotlin.math.roundToInt


/**
 * Class: [PreviewPageFragment]
 *
 * TODO:// Crash https://bangumi.tv/blog/327666
 *
 * @author why
 * @since 12/1/23
 */
class PreviewPageFragment :
    BaseViewModelFragment<ActivityPreviewPageBinding, PreviewPageViewModel>() {

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.imageUrl = arguments.getString(NavKey.KEY_STRING).orEmpty()
        viewModel.position = arguments.getInt(NavKey.KEY_INTEGER)
        viewModel.total = arguments.getInt(NavKey.KEY_INTEGER_SECOND)
    }

    override fun initView() {
        binding.ivImage.isQuickScaleEnabled = true
        if (viewModel.isLoadNormal) {
            binding.ivGif.isVisible = true
            binding.ivImage.isVisible = false
        } else {
            binding.ivGif.isVisible = false
            binding.ivImage.isVisible = true
        }
    }

    override fun initData() {
        if (viewModel.isLoadNormal) {
            binding.ivGif.loadImage(viewModel.imageUrl, cropOrFit = false)
            binding.gpProgress.isVisible = false
        } else {
            val target = ImageProgressTarget(
                viewModel.imageUrl, requireContext(),
                SubsamplingTarget(binding.ivImage)
            )

            Glide.with(requireContext())
                .asFile()
                .load(viewModel.imageUrl)
                .into(target)
        }
    }

    override fun initListener() {
        binding.ivImage.setOnImageEventListener(object : SubsamplingEventListener() {
            override fun onImageLoaded() {
                if (isDetached) return

                val scale = binding.ivImage.scale
                binding.ivImage.minScale = scale
                binding.ivImage.maxScale = scale * 5f
                binding.ivImage.setDoubleTapZoomScale(scale * 2.5f)
                binding.ivImage.resetScaleAndCenter()
            }
        })
        binding.ivImage.setOnLongClickListener {
            if (isDetached || activity == null) return@setOnLongClickListener true

            showActionDialog(viewModel.imageUrl)
            true
        }
    }

    private fun showActionDialog(url: String) {
        MaterialAlertDialogBuilder(requireActivity())
            .setItems(viewModel.shareMenus) { _, which ->
                viewModel.downloadWallpaperFromUrl(url, viewModel.shareMenus[which])
            }
            .create()
            .show()
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

    override fun onDestroyView() {
        binding.ivImage.recycle()
        super.onDestroyView()
    }

    companion object {
        fun newInstance(imageUrl: String, position: Int, total: Int): PreviewPageFragment {
            return PreviewPageFragment().apply {
                arguments = bundleOf(
                    NavKey.KEY_STRING to imageUrl,
                    NavKey.KEY_INTEGER to position,
                    NavKey.KEY_INTEGER_SECOND to total
                )
            }
        }
    }
}