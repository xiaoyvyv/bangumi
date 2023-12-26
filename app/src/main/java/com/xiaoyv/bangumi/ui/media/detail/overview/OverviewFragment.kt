package com.xiaoyv.bangumi.ui.media.detail.overview

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.xiaoyv.bangumi.databinding.FragmentOverviewBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.bangumi.ui.media.action.MediaEpActionDialog
import com.xiaoyv.bangumi.ui.media.action.MediaSaveActionDialog
import com.xiaoyv.bangumi.ui.media.detail.MediaDetailViewModel
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.api.response.api.ApiUserEpEntity
import com.xiaoyv.common.api.response.douban.DouBanPhotoEntity
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.MediaDetailType
import com.xiaoyv.common.config.annotation.TopicType
import com.xiaoyv.common.config.bean.AdapterTypeItem
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.helper.callback.RecyclerItemTouchedListener
import com.xiaoyv.common.kts.forceCast
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.common.widget.scroll.AnimeLinearLayoutManager
import com.xiaoyv.widget.dialog.UiDialog

/**
 * Class: [OverviewFragment]
 *
 * @author why
 * @since 11/24/23
 */
class OverviewFragment : BaseViewModelFragment<FragmentOverviewBinding, OverviewViewModel>() {

    private val activityViewModel by activityViewModels<MediaDetailViewModel>()

    private val touchedListener = RecyclerItemTouchedListener {
        activityViewModel.vpEnableLiveData.value = it
    }

    private val overviewAdapter by lazy {
        OverviewAdapter(
            touchedListener = touchedListener,
            onClickSave = click@{ item, _ ->
                // 条目锁定了
                if (activityViewModel.requireNotLocked.not()) {
                    showLockTip()
                    return@click
                }
                showCollectPanel(item)
            },
            onClickEpItem = click@{ adapter, _, position ->
                // 条目锁定了
                if (activityViewModel.requireNotLocked.not()) {
                    showLockTip()
                    return@click
                }

                // 章节进度弹窗
                val epEntity = adapter.getItem(position)
                if (epEntity != null && epEntity.splitter.not()) {
                    if (viewModel.canChangeEpProgress) {
                        showEpCollectDialog(epEntity)
                    } else {
                        RouteHelper.jumpTopicDetail(epEntity.id, TopicType.TYPE_EP)
                    }
                }
            },
            onClickEpAdd = { entity, isAddEp ->
                autoIncreaseProgress(entity, isAddEp)
            },
            onClickCrtItem = {
                RouteHelper.jumpPerson(it.id, true)
            },
            onClickTagItem = {
                RouteHelper.jumpTagDetail(it.mediaType, it.tagName)
            },
            onClickRelatedItem = {
                RouteHelper.jumpMediaDetail(it.id)
            },
            onClickCollectorItem = {
                RouteHelper.jumpUserDetail(it.id)
            },
            onClickIndexItem = {
                RouteHelper.jumpIndexDetail(it.id)
            },
            onClickPreview = {
                showPreview(it)
            },
            onClickTour = {
                RouteHelper.jumpWeb(
                    url = "https://anitabi.cn/map?bangumiId=" + viewModel.mediaId,
                    fitToolbar = true,
                    smallToolbar = true
                )
            },
            onClickCommentItem = {
                RouteHelper.jumpUserDetail(it.userId)
            },
            onClickCommentUser = {
                RouteHelper.jumpUserDetail(it.userId)
            }
        )
    }

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.mediaId = arguments.getString(NavKey.KEY_STRING).orEmpty()
    }

    override fun initView() {

    }

    override fun initData() {
        binding.rvRecycler.layoutManager =
            AnimeLinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false).apply {
                extraLayoutSpaceScale = 8f
            }
        binding.rvRecycler.adapter = overviewAdapter
        binding.rvRecycler.itemAnimator = null
    }

    override fun initListener() {
        overviewAdapter.setOnDebouncedChildClickListener(com.xiaoyv.common.R.id.tv_more) {
            when (it.type) {
                OverviewAdapter.TYPE_COLLECT -> {
                    requireActivity().showConfirmDialog(
                        message = "是否删除该收藏？",
                        onConfirmClick = {
                            viewModel.deleteCollect()
                        }
                    )
                }

                OverviewAdapter.TYPE_EP -> {
                    activityViewModel.vpCurrentItemType.value = MediaDetailType.TYPE_CHAPTER
                }

                OverviewAdapter.TYPE_CHARACTER -> {
                    activityViewModel.vpCurrentItemType.value = MediaDetailType.TYPE_CHARACTER
                }

                OverviewAdapter.TYPE_COMMENT -> {
                    activityViewModel.vpCurrentItemType.value = MediaDetailType.TYPE_COMMENTS
                }

                OverviewAdapter.TYPE_RATING -> {
                    RouteHelper.jumpRatingDetail()
                }

                OverviewAdapter.TYPE_SUMMARY -> {
                    RouteHelper.jumpSummaryDetail(it.entity.forceCast<MediaDetailEntity>().subjectSummary)
                }

                OverviewAdapter.TYPE_DETAIL -> {
                    RouteHelper.jumpSummaryDetail(*it.entity.forceCast<MediaDetailEntity>().infoHtml.toTypedArray())
                }

                OverviewAdapter.TYPE_PREVIEW -> {
                    RouteHelper.jumpMediaPreview(viewModel.targetId)
                }

                OverviewAdapter.TYPE_TOUR -> {
                    RouteHelper.jumpWeb(
                        url = "https://anitabi.cn/map?bangumiId=" + viewModel.mediaId,
                        fitToolbar = true,
                        smallToolbar = true
                    )
                }
            }
        }

        overviewAdapter.setOnDebouncedChildClickListener(com.xiaoyv.common.R.id.tv_summary_content) {
            when (it.type) {
                OverviewAdapter.TYPE_SUMMARY -> {
                    RouteHelper.jumpSummaryDetail(it.entity.forceCast<MediaDetailEntity>().subjectSummary)
                }

                OverviewAdapter.TYPE_DETAIL -> {
                    RouteHelper.jumpSummaryDetail(*it.entity.forceCast<MediaDetailEntity>().infoHtml.toTypedArray())
                }
            }
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        binding.stateView.initObserver(
            lifecycleOwner = this,
            loadingBias = 0.3f,
            loadingViewState = viewModel.loadingViewState
        )

        viewModel.mediaDetailLiveData.observe(this) {
            activityViewModel.onMediaDetailLiveData.value = it

            val title = it?.titleCn?.ifBlank { it.titleNative }
            if (title.isNullOrBlank().not()) {
                viewModel.queryPhotos(title)
            }
        }

        viewModel.mediaBinderListLiveData.observe(this) {
            overviewAdapter.submitList(it)
        }

        // 监听刷新预览数据结果
        viewModel.onMediaPreviewLiveData.observe(this) {
            val photos = it ?: return@observe
            overviewAdapter.refreshPhotos(photos)
        }

        // 监听刷新章节格子结果
        viewModel.onRefreshEpLiveData.observe(this) {
            it ?: return@observe
            overviewAdapter.refreshEpList(it.first, it.second, it.third)
        }

        // 监听删除条目收藏结果
        viewModel.onDeleteCollectLiveData.observe(this) {
            it ?: return@observe
            val entity = viewModel.refreshCollectState(it) ?: return@observe
            overviewAdapter.refreshCollect(entity)

            // 刷新 HostActivity 的媒体数据
            activityViewModel.onMediaDetailLiveData.value = entity
        }

        // 巡礼数据
        viewModel.onTourLiveData.observe(this) {
            if (viewModel.mediaBinderListLiveData.value != null && it != null) {
                overviewAdapter.refreshTour(it)
            }
        }

        // 用户身份信息变化刷新
        UserHelper.observeUserInfo(this) {
            viewModel.queryMediaInfo()
        }

        // 刷新章节数据
        UserHelper.observeAction(this) {
            if (it == BgmPathType.TYPE_EP) {
                viewModel.refreshEpList()
            }
        }
    }

    /**
     * 自动增加进度
     */
    private fun autoIncreaseProgress(entity: MediaDetailEntity, addEp: Boolean) {
        if (addEp) {
            viewModel.progressIncrease(entity.progress + 1, entity.progressSecond)
        } else {
            viewModel.progressIncrease(entity.progress, entity.progressSecond + 1)
        }
    }

    /**
     * 章节进度弹窗
     */
    private fun showEpCollectDialog(chapterEntity: ApiUserEpEntity) {
        // 收藏
        MediaEpActionDialog.show(
            fragmentManager = childFragmentManager,
            epEntity = chapterEntity,
            mediaType = activityViewModel.requireMediaType
        )
    }

    private fun showLockTip() {
        requireActivity().showConfirmDialog(
            message = "不符合收录原则，条目及相关收藏、讨论、关联等内容将会随时被移除。",
            cancelText = null
        )
    }

    /**
     * 条目收藏弹窗
     */
    private fun showCollectPanel(item: AdapterTypeItem) {
        if (!UserHelper.isLogin) {
            RouteHelper.jumpLogin()
            return
        }

        MediaSaveActionDialog.show(
            childFragmentManager,
            item.entity.forceCast<MediaDetailEntity>().collectState,
            activityViewModel.requireMediaType
        ) {
            val entity = viewModel.refreshCollectState(it) ?: return@show
            overviewAdapter.refreshCollect(entity)

            // 刷新 HostActivity 的媒体数据
            activityViewModel.onMediaDetailLiveData.value = entity
        }
    }

    private fun showPreview(photo: DouBanPhotoEntity.Photo) {
        val item = overviewAdapter.items.find { it.type == OverviewAdapter.TYPE_PREVIEW } ?: return
        val photos = item.entity.forceCast<List<DouBanPhotoEntity.Photo>>()
        val showImageUrl = photo.image?.large?.url.orEmpty()
        val totalImageUrls = photos.map { it.image?.large?.url.orEmpty() }
        RouteHelper.jumpPreviewImage(showImageUrl, totalImageUrls)
    }

    override fun createLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(requireActivity())
    }

    companion object {
        fun newInstance(mediaId: String): OverviewFragment {
            return OverviewFragment().apply {
                arguments = bundleOf(NavKey.KEY_STRING to mediaId)
            }
        }
    }
}