package com.xiaoyv.bangumi.ui.process.page

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseDifferAdapter
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListFragment
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.bangumi.ui.media.action.MediaEpActionDialog
import com.xiaoyv.bangumi.ui.media.action.MediaEpActionDialog.Companion.watched
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.common.widget.scroll.AnimeLinearLayoutManager
import com.xiaoyv.widget.dialog.UiDialog

/**
 * Class: [ProcessPageFragment]
 *
 * @author why
 * @since 12/24/23
 */
class ProcessPageFragment : BaseListFragment<MediaDetailEntity, ProcessPageViewModel>() {
    override val isOnlyOnePage: Boolean
        get() = true

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.mediaType = arguments.getString(NavKey.KEY_STRING).orEmpty()
    }

    override fun initListener() {
        super.initListener()
        contentAdapter.setOnDebouncedChildClickListener(R.id.iv_cover) {
            RouteHelper.jumpMediaDetail(it.id)
        }
    }

    override fun onCreateLayoutManager(): LinearLayoutManager {
        return AnimeLinearLayoutManager(hostActivity, LinearLayoutManager.VERTICAL, false).apply {
            extraLayoutSpaceScale = 5f
        }
    }

    override fun onCreateContentAdapter(): BaseDifferAdapter<MediaDetailEntity, *> {
        return ProcessPageAdapter(viewModel.mediaType,
            clickItemListener = { adapter, _, position ->
                val epEntity = adapter.getItem(position) ?: return@ProcessPageAdapter
                MediaEpActionDialog.show(
                    fragmentManager = childFragmentManager,
                    epEntity = epEntity,
                    watchedIds = adapter.items.watched(epEntity),
                    mediaType = viewModel.mediaType
                )
            },
            clickAddEpProgress = { entity, isAddEp ->
                autoIncreaseProgress(entity, isAddEp)
            }
        )
    }

    override fun LifecycleOwner.initViewObserverExt() {
        UserHelper.observeAction(this) {
            if (it == BgmPathType.TYPE_EP && isVisible) {
                viewModel.refresh()
            }
        }
    }

    private fun autoIncreaseProgress(entity: MediaDetailEntity, addEp: Boolean) {
        if (addEp) {
            viewModel.progressIncrease(entity, entity.progress + 1, entity.progressSecond)
        } else {
            viewModel.progressIncrease(entity, entity.progress, entity.progressSecond + 1)
        }
    }

    override fun createLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(requireActivity())
    }

    companion object {
        fun newInstance(@MediaType mediaType: String): ProcessPageFragment {
            return ProcessPageFragment().apply {
                arguments = bundleOf(NavKey.KEY_STRING to mediaType)
            }
        }
    }
}