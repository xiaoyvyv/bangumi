package com.xiaoyv.bangumi.ui.media.detail.score.page

import android.os.Bundle
import androidx.core.os.bundleOf
import com.chad.library.adapter.base.BaseDifferAdapter
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListFragment
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.MediaScoreEntity
import com.xiaoyv.common.config.annotation.InterestType
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener

/**
 * Class: [MediaScorePageFragment]
 *
 * @author why
 * @since 1/22/24
 */
class MediaScorePageFragment : BaseListFragment<MediaScoreEntity, MediaScorePageViewModel>() {
    override val isOnlyOnePage: Boolean
        get() = false

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.mediaId = arguments.getString(NavKey.KEY_STRING).orEmpty()
        viewModel.interestType = arguments.getString(NavKey.KEY_STRING_SECOND).orEmpty()
        viewModel.forFriend = arguments.getBoolean(NavKey.KEY_BOOLEAN)
    }

    override fun onCreateContentAdapter(): BaseDifferAdapter<MediaScoreEntity, *> {
        return MediaScorePageAdapter()
    }

    override fun initListener() {
        super.initListener()

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_score) {
            RouteHelper.jumpUserDetail(it.id)
        }
    }

    companion object {
        fun newInstance(
            mediaId: String,
            friend: Boolean = false,
            @InterestType interestType: String,
        ): MediaScorePageFragment {
            return MediaScorePageFragment().apply {
                arguments = bundleOf(
                    NavKey.KEY_STRING to mediaId,
                    NavKey.KEY_STRING_SECOND to interestType,
                    NavKey.KEY_BOOLEAN to friend
                )
            }
        }
    }
}