package com.xiaoyv.bangumi.ui.media.detail.comments

import android.os.Bundle
import androidx.core.os.bundleOf
import com.xiaoyv.bangumi.base.BaseListFragment
import com.xiaoyv.bangumi.ui.media.detail.chapter.MediaChapterFragment
import com.xiaoyv.bangumi.ui.media.detail.chapter.MediaChapterViewModel
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.MediaCommentEntity
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [MediaCommentFragment]
 *
 * @author why
 * @since 11/24/23
 */
class MediaCommentFragment : BaseListFragment<MediaCommentEntity, MediaCommentViewModel>() {
    override fun initArgumentsData(arguments: Bundle) {
        viewModel.mediaId = arguments.getString(NavKey.KEY_STRING).orEmpty()
    }

    override val isOnlyOnePage: Boolean
        get() = true

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<MediaCommentEntity, *> {
        return MediaCommentAdapter()
    }

    companion object {
        fun newInstance(mediaId: String): MediaCommentFragment {
            return MediaCommentFragment().apply {
                arguments = bundleOf(NavKey.KEY_STRING to mediaId)
            }
        }
    }
}