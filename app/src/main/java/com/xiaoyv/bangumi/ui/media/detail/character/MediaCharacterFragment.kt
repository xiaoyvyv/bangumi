package com.xiaoyv.bangumi.ui.media.detail.character

import android.os.Bundle
import androidx.core.os.bundleOf
import com.xiaoyv.bangumi.base.BaseListFragment
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.MediaCharacterEntity
import com.xiaoyv.common.api.parser.entity.MediaMakerEntity
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [MediaCharacterFragment]
 *
 * @author why
 * @since 11/24/23
 */
class MediaCharacterFragment : BaseListFragment<MediaCharacterEntity, MediaCharacterViewModel>() {
    override fun initArgumentsData(arguments: Bundle) {
        viewModel.mediaId = arguments.getString(NavKey.KEY_STRING).orEmpty()
    }

    override val isOnlyOnePage: Boolean
        get() = true

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<MediaCharacterEntity, *> {
        return MediaCharacterAdapter()
    }

    companion object {
        fun newInstance(mediaId: String): MediaCharacterFragment {
            return MediaCharacterFragment().apply {
                arguments = bundleOf(NavKey.KEY_STRING to mediaId)
            }
        }
    }
}