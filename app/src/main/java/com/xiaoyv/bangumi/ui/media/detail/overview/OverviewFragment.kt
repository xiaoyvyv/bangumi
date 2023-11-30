@file:Suppress("UNCHECKED_CAST")

package com.xiaoyv.bangumi.ui.media.detail.overview

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.databinding.FragmentOverviewBinding
import com.xiaoyv.bangumi.ui.media.detail.MediaDetailViewModel
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewBoardBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewCharacterBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewCommentBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewDetailBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewEpBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewIndexBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewMakerBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewPreviewBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewRatingBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewRelativeBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewReviewBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewSaveBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewSummaryBinder
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewTagBinder
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.helper.RecyclerItemTouchedListener
import com.xiaoyv.widget.binder.BaseQuickBindingHolder

/**
 * Class: [OverviewFragment]
 *
 * @author why
 * @since 11/24/23
 */
class OverviewFragment : BaseViewModelFragment<FragmentOverviewBinding, OverviewViewModel>() {

    private val mediaViewModel by activityViewModels<MediaDetailViewModel>()

    private val touchedListener = RecyclerItemTouchedListener {
        mediaViewModel.vpEnableLiveData.value = it
    }

    private var viewHolders: HashMap<Int, BaseQuickBindingHolder<*>> = hashMapOf()

    /**
     * RecyclerView 多 Type 效果不太好，这里改写直接进入全部渲染
     */
    private val viewBinderTypes: HashMap<Int, BaseMultiItemAdapter.OnMultiItemAdapterListener<OverviewAdapter.OverviewItem, *>> by lazy {
        hashMapOf<Int, BaseMultiItemAdapter.OnMultiItemAdapterListener<OverviewAdapter.OverviewItem, *>>().apply {
            put(OverviewAdapter.TYPE_SAVE, OverviewSaveBinder())
            put(OverviewAdapter.TYPE_EP, OverviewEpBinder(touchedListener))
            put(OverviewAdapter.TYPE_TAG, OverviewTagBinder())
            put(OverviewAdapter.TYPE_SUMMARY, OverviewSummaryBinder())
            put(OverviewAdapter.TYPE_PREVIEW, OverviewPreviewBinder())
            put(OverviewAdapter.TYPE_DETAIL, OverviewDetailBinder())
            put(OverviewAdapter.TYPE_RATING, OverviewRatingBinder())
            put(OverviewAdapter.TYPE_CHARACTER, OverviewCharacterBinder(touchedListener))
            put(OverviewAdapter.TYPE_MAKER, OverviewMakerBinder())
            put(OverviewAdapter.TYPE_RELATIVE, OverviewRelativeBinder(touchedListener))
            put(OverviewAdapter.TYPE_INDEX, OverviewIndexBinder(touchedListener))
            put(OverviewAdapter.TYPE_REVIEW, OverviewReviewBinder(touchedListener))
            put(OverviewAdapter.TYPE_BOARD, OverviewBoardBinder(touchedListener))
            put(OverviewAdapter.TYPE_COMMENT, OverviewCommentBinder(touchedListener))
        }
    }

    private val overviewAdapter by lazy {
        OverviewAdapter(RecyclerItemTouchedListener {
            mediaViewModel.vpEnableLiveData.value = it
        })
    }

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.mediaId = arguments.getString(NavKey.KEY_STRING).orEmpty()
    }

    override fun initView() {


    }

    override fun initData() {
        viewModel.queryMediaInfo()

        // 手动创建
        viewHolders.clear()
        binding.llContainer.removeAllViews()
        viewBinderTypes.forEach { (key, listener) ->
            val holder = listener.onCreate(hostActivity, binding.llContainer, key)
            binding.llContainer.addView(holder.itemView)
            viewHolders[key] = holder as BaseQuickBindingHolder<*>
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.mediaDetailLiveData.observe(this) {
            mediaViewModel.onMediaDetailLiveData.value = it
        }

        viewModel.mediaBinderListLiveData.observe(this) {
            // 手动填充数据
            it.forEachIndexed { index, overviewItem ->
                val adapterListener =
                    viewBinderTypes[overviewItem.type] as? BaseMultiItemAdapter.OnMultiItemAdapterListener<OverviewAdapter.OverviewItem, BaseQuickBindingHolder<*>>
                val bindingHolder = viewHolders[overviewItem.type]
                if (bindingHolder != null && adapterListener != null) {
                    adapterListener.onBind(bindingHolder, index, overviewItem)
                }
            }
        }
    }

    override fun initListener() {

    }

    companion object {
        fun newInstance(mediaId: String): OverviewFragment {
            return OverviewFragment().apply {
                arguments = bundleOf(NavKey.KEY_STRING to mediaId)
            }
        }
    }
}