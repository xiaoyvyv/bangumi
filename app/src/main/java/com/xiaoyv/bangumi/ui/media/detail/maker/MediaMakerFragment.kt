package com.xiaoyv.bangumi.ui.media.detail.maker

import android.os.Bundle
import androidx.core.os.bundleOf
import com.xiaoyv.bangumi.base.BaseListFragment
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.MediaCommentEntity
import com.xiaoyv.common.api.parser.entity.MediaMakerEntity
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [MediaMakerFragment]
 *
 * @author why
 * @since 11/24/23
 */
class MediaMakerFragment : BaseListFragment<MediaMakerEntity, MediaMakerViewModel>() {
    override fun initArgumentsData(arguments: Bundle) {
        viewModel.mediaId = arguments.getString(NavKey.KEY_STRING).orEmpty()
    }

    override val isOnlyOnePage: Boolean
        get() = true

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<MediaMakerEntity, *> {
        return MediaMakerAdapter()
    }

    companion object {
        fun newInstance(mediaId: String): MediaMakerFragment {
            return MediaMakerFragment().apply {
                arguments = bundleOf(NavKey.KEY_STRING to mediaId)
            }
        }
    }
}