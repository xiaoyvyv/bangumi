package com.xiaoyv.bangumi.ui.media.detail.chapter

import android.os.Bundle
import androidx.core.os.bundleOf
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListFragment
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.MediaChapterEntity
import com.xiaoyv.common.config.annotation.TopicType
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [MediaChapterFragment]
 *
 * @author why
 * @since 11/24/23
 */
class MediaChapterFragment : BaseListFragment<MediaChapterEntity, MediaChapterViewModel>() {
    override fun initArgumentsData(arguments: Bundle) {
        viewModel.mediaId = arguments.getString(NavKey.KEY_STRING).orEmpty()
    }

    override val isOnlyOnePage: Boolean
        get() = true

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<MediaChapterEntity, *> {
        return MediaChapterAdapter()
    }

    override fun initListener() {
        super.initListener()
        
        contentAdapter.setOnDebouncedChildClickListener(R.id.item_ep) {
            RouteHelper.jumpTopicDetail(it.id, TopicType.TYPE_EP)
        }
    }

    companion object {
        fun newInstance(mediaId: String): MediaChapterFragment {
            return MediaChapterFragment().apply {
                arguments = bundleOf(NavKey.KEY_STRING to mediaId)
            }
        }
    }
}