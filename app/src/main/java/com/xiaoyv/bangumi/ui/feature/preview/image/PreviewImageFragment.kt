package com.xiaoyv.bangumi.ui.feature.preview.image

import android.os.Bundle
import androidx.core.os.bundleOf
import com.xiaoyv.bangumi.databinding.ActivityPreviewPageBinding
import com.xiaoyv.blueprint.base.binding.BaseBindingFragment
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.kts.loadImageAnimate

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

    override fun initArgumentsData(arguments: Bundle) {
        imageUrl = arguments.getString(NavKey.KEY_STRING).orEmpty()
        position = arguments.getInt(NavKey.KEY_INTEGER)
        total = arguments.getInt(NavKey.KEY_INTEGER_SECOND)
    }

    override fun initView() {
        binding.ivImage.loadImageAnimate(imageUrl, centerCrop = false)
    }

    override fun initData() {

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