package com.xiaoyv.bangumi.ui.media.detail.review

import android.os.Bundle
import androidx.core.os.bundleOf
import com.xiaoyv.bangumi.base.BaseListFragment
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.MediaCommentEntity
import com.xiaoyv.common.api.parser.entity.MediaReviewEntity
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [MediaReviewFragment]
 *
 * @author why
 * @since 11/24/23
 */
class MediaReviewFragment : BaseListFragment<MediaReviewEntity, MediaReviewViewModel>() {
    override fun initArgumentsData(arguments: Bundle) {
        viewModel.mediaId = arguments.getString(NavKey.KEY_STRING).orEmpty()
    }

    override val isOnlyOnePage: Boolean
        get() = false

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<MediaReviewEntity, *> {
        return MediaReviewAdapter()
    }

    companion object {
        fun newInstance(mediaId: String): MediaReviewFragment {
            return MediaReviewFragment().apply {
                arguments = bundleOf(NavKey.KEY_STRING to mediaId)
            }
        }
    }
}