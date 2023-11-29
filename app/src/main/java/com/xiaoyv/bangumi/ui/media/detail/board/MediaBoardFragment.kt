package com.xiaoyv.bangumi.ui.media.detail.board

import android.os.Bundle
import androidx.core.os.bundleOf
import com.xiaoyv.bangumi.base.BaseListFragment
import com.xiaoyv.bangumi.ui.media.detail.review.MediaReviewFragment
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.MediaBoardEntity
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [MediaBoardFragment]
 *
 * @author why
 * @since 11/24/23
 */
class MediaBoardFragment : BaseListFragment<MediaBoardEntity, MediaBoardViewModel>() {
    override fun initArgumentsData(arguments: Bundle) {
        viewModel.mediaId = arguments.getString(NavKey.KEY_STRING).orEmpty()
    }

    override val isOnlyOnePage: Boolean
        get() = true

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<MediaBoardEntity, *> {
        return MediaBoardAdapter()
    }

    companion object {
        fun newInstance(mediaId: String): MediaReviewFragment {
            return MediaReviewFragment().apply {
                arguments = bundleOf(NavKey.KEY_STRING to mediaId)
            }
        }
    }
}