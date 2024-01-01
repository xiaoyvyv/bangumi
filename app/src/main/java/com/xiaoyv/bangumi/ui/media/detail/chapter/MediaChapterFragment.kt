package com.xiaoyv.bangumi.ui.media.detail.chapter

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListFragment
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.bangumi.ui.media.action.MediaEpActionDialog
import com.xiaoyv.bangumi.ui.media.action.MediaEpActionDialog.Companion.watched
import com.xiaoyv.bangumi.ui.media.detail.MediaDetailViewModel
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.response.api.ApiUserEpEntity
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.InterestType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.config.annotation.TopicType
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [MediaChapterFragment]
 *
 * @author why
 * @since 11/24/23
 */
class MediaChapterFragment : BaseListFragment<ApiUserEpEntity, MediaChapterViewModel>() {
    private val activityViewModel by activityViewModels<MediaDetailViewModel>()

    override val isOnlyOnePage: Boolean
        get() = true

    override val loadingBias: Float
        get() = 0.3f

    override val scrollTopWhenRefresh: Boolean
        get() = false

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.mediaId = arguments.getString(NavKey.KEY_STRING).orEmpty()
        viewModel.activityViewModel = activityViewModel
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<ApiUserEpEntity, *> {
        return MediaChapterAdapter()
    }

    override fun initListener() {
        super.initListener()

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_ep) {
            // 不支持编辑进度直接打开讨论话题
            if (MediaType.canEditEpProgress(activityViewModel.requireMediaType).not()
                || viewModel.activityViewModel.requireMediaCollectType == InterestType.TYPE_UNKNOWN
            ) {
                RouteHelper.jumpTopicDetail(it.id, TopicType.TYPE_EP)
                return@setOnDebouncedChildClickListener
            }


            // 弹窗
            MediaEpActionDialog.show(
                fragmentManager = childFragmentManager,
                epEntity = it,
                watchedIds = contentAdapter.items.watched(it),
                mediaType = activityViewModel.requireMediaType
            )
        }

        contentAdapter.setOnDebouncedChildClickListener(R.id.tv_comment) {
            RouteHelper.jumpTopicDetail(it.id, TopicType.TYPE_EP)
        }

        // 搜索资源
        contentAdapter.addOnItemChildLongClickListener(R.id.tv_comment) { adapter, _, position ->
            val entity = adapter.getItem(position) ?: return@addOnItemChildLongClickListener true
            val episode = entity.episode ?: return@addOnItemChildLongClickListener true
            val name = episode.nameCn.orEmpty().ifBlank { episode.name.orEmpty() }
            val ep = episode.ep.let { if (it.length == 1) "0$it" else it }

            RouteHelper.jumpAnimeMagnet("$name $ep")
            true
        }
    }

    override fun LifecycleOwner.initViewObserverExt() {
        UserHelper.observeAction(this) {
            if (it == BgmPathType.TYPE_EP) {
                viewModel.refresh()
            }
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