package com.xiaoyv.bangumi.ui.media.detail.character

import android.os.Bundle
import androidx.core.os.bundleOf
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListFragment
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.MediaCharacterEntity
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [MediaCharacterFragment]
 *
 * @author why
 * @since 11/24/23
 */
class MediaCharacterFragment : BaseListFragment<MediaCharacterEntity, MediaCharacterViewModel>() {
    override val isOnlyOnePage: Boolean
        get() = true

    override val loadingBias: Float
        get() = 0.3f

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.mediaId = arguments.getString(NavKey.KEY_STRING).orEmpty()
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<MediaCharacterEntity, *> {
        return MediaCharacterAdapter()
    }

    override fun initListener() {
        super.initListener()

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_person) {
            RouteHelper.jumpPerson(it.id, true)
        }
    }

    companion object {
        fun newInstance(mediaId: String): MediaCharacterFragment {
            return MediaCharacterFragment().apply {
                arguments = bundleOf(NavKey.KEY_STRING to mediaId)
            }
        }
    }
}