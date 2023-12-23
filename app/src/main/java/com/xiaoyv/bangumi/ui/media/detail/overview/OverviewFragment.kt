package com.xiaoyv.bangumi.ui.media.detail.overview

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.xiaoyv.bangumi.databinding.FragmentOverviewBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.bangumi.ui.media.action.MediaEpCollectDialog
import com.xiaoyv.bangumi.ui.media.action.MediaSaveActionDialog
import com.xiaoyv.bangumi.ui.media.detail.MediaDetailViewModel
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.MediaChapterEntity
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.api.response.douban.DouBanPhotoEntity
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.InterestType
import com.xiaoyv.common.config.annotation.MediaDetailType
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
            onClickSave = { item, position ->
                showCollectPanel(item, position)
            },
            onClickEpItem = { adapter, _, position ->
                val chapterEntity = adapter.getItem(position)
                if (chapterEntity != null && chapterEntity.splitter.not() && viewModel.canChangeEpProgress) {
                    showEpCollectDialog(chapterEntity)
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

        viewModel.onMediaPreviewLiveData.observe(this) {
            val photos = it ?: return@observe
            overviewAdapter.refreshPhotos(photos)
        }

        viewModel.onRefreshEpLiveData.observe(this) {
            it ?: return@observe
            overviewAdapter.refreshEpList(it.first, it.second, it.third)
        }

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
     * 章节收藏弹窗，仅动画或三次元有这个章节格子
     */
    private fun showEpCollectDialog(chapterEntity: MediaChapterEntity) {
        if (activityViewModel.requireMediaCollectType != InterestType.TYPE_DO) {
            requireActivity().showConfirmDialog(
                message = "只有收藏为 (在看 | 在玩 | 在读 | 在听) 的条目才可以单独修改章节进度",
                cancelText = null
            )
            return
        }

        // 收藏
        MediaEpCollectDialog.show(
            fragmentManager = childFragmentManager,
            chapterEntity = chapterEntity,
            mediaType = activityViewModel.requireMediaType
        ) { epList, progress ->
            overviewAdapter.refreshEpList(epList, progress, 0)
        }
    }

    /**
     * 条目收藏弹窗
     */
    private fun showCollectPanel(item: AdapterTypeItem, position: Int) {
        if (!UserHelper.isLogin) {
            RouteHelper.jumpLogin()
            return
        }

        val media = item.entity.forceCast<MediaDetailEntity>()
        MediaSaveActionDialog.show(
            childFragmentManager,
            media.collectState,
            activityViewModel.requireMediaType
        ) {
            val entity = viewModel.refreshCollectState(it) ?: return@show
            item.entity = entity
            // 刷新收藏的 Item
            overviewAdapter[position] = item

            // 刷新章节的进度 Item
            overviewAdapter.getItem(position + 1)?.entity = entity
            overviewAdapter.notifyItemChanged(position + 1)

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