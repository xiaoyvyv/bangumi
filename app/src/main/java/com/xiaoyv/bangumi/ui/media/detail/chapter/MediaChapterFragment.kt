package com.xiaoyv.bangumi.ui.media.detail.chapter

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListFragment
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.bangumi.ui.media.action.MediaEpCollectDialog
import com.xiaoyv.bangumi.ui.media.detail.MediaDetailViewModel
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.MediaChapterEntity
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.InterestType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.config.annotation.TopicType
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [MediaChapterFragment]
 *
 * @author why
 * @since 11/24/23
 */
class MediaChapterFragment : BaseListFragment<MediaChapterEntity, MediaChapterViewModel>() {
    private val activityViewModel by activityViewModels<MediaDetailViewModel>()

    override val isOnlyOnePage: Boolean
        get() = true

    override val loadingBias: Float
        get() = 0.3f

    override val scrollTopWhenRefresh: Boolean
        get() = false

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.mediaId = arguments.getString(NavKey.KEY_STRING).orEmpty()
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<MediaChapterEntity, *> {
        return MediaChapterAdapter()
    }

    override fun initListener() {
        super.initListener()

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_ep) {
            if (it.splitter) return@setOnDebouncedChildClickListener

            // 不支持编辑进度直接打开讨论话题
            if (!MediaType.canEditEpProgress(activityViewModel.requireMediaType)) {
                RouteHelper.jumpTopicDetail(it.id, TopicType.TYPE_EP)
                return@setOnDebouncedChildClickListener
            }

            // 校验收藏状态是否可以编辑进度
            if (activityViewModel.requireMediaCollectType != InterestType.TYPE_DO) {
                requireActivity().showConfirmDialog(
                    message = "只有收藏为 (在看 | 在玩 | 在读 | 在听) 的条目才可以单独修改章节进度",
                    cancelText = null
                )
                return@setOnDebouncedChildClickListener
            }

            // 弹窗
            MediaEpCollectDialog.show(
                fragmentManager = childFragmentManager,
                chapterEntity = it,
                mediaType = activityViewModel.requireMediaType
            ) { entities, _ ->
                viewModel.onListLiveData.value = entities
            }
        }

        contentAdapter.setOnDebouncedChildClickListener(R.id.tv_comment) {
            RouteHelper.jumpTopicDetail(it.id, TopicType.TYPE_EP)
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