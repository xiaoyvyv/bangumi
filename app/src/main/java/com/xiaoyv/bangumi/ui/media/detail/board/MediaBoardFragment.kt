package com.xiaoyv.bangumi.ui.media.detail.board

import android.os.Bundle
import androidx.core.os.bundleOf
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListFragment
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.MediaBoardEntity
import com.xiaoyv.common.config.annotation.TopicType
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [MediaBoardFragment]
 *
 * @author why
 * @since 11/24/23
 */
class MediaBoardFragment : BaseListFragment<MediaBoardEntity, MediaBoardViewModel>() {
    override val isOnlyOnePage: Boolean
        get() = true

    override val loadingBias: Float
        get() = 0.3f

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.mediaId = arguments.getString(NavKey.KEY_STRING).orEmpty()
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<MediaBoardEntity, *> {
        return MediaBoardAdapter()
    }

    override fun initListener() {
        super.initListener()

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_board) {
            RouteHelper.jumpTopicDetail(it.id, TopicType.TYPE_SUBJECT)
        }
    }

    companion object {
        fun newInstance(mediaId: String): MediaBoardFragment {
            return MediaBoardFragment().apply {
                arguments = bundleOf(NavKey.KEY_STRING to mediaId)
            }
        }
    }
}