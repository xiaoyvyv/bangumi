@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.ui.profile.page.save

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.core.view.updateLayoutParams
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListFragment
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.BrowserEntity
import com.xiaoyv.common.config.GlobalConfig
import com.xiaoyv.common.config.annotation.InterestCollectType
import com.xiaoyv.common.config.annotation.InterestType
import com.xiaoyv.common.config.bean.FilterEntity
import com.xiaoyv.common.databinding.ViewSaveListFilterBinding
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.CommonId
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.widget.dialog.filter.FilterOptionDialog
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.callback.setOnFastLimitClickListener

/**
 * Class: [SaveListFragment]
 *
 * @author why
 * @since 11/24/23
 */
class SaveListFragment : BaseListFragment<BrowserEntity.Item, SaveListViewModel>() {
    private lateinit var filter: ViewSaveListFilterBinding

    override val isOnlyOnePage: Boolean
        get() = false

    override val loadingBias: Float
        get() = 0.3f

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<BrowserEntity.Item, *> {
        return SaveListAdapter()
    }

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.userId = arguments.getString(NavKey.KEY_STRING).orEmpty()
        viewModel.requireLogin = arguments.getBoolean(NavKey.KEY_BOOLEAN, false)
    }

    override fun initView() {
        super.initView()

        // 过滤菜单
        filter = ViewSaveListFilterBinding.inflate(layoutInflater, binding.flContainer, true)
        filter.root.doOnPreDraw {
            binding.rvContent.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = filter.root.height
            }
        }

        // 切换
        filter.listType.setOnCheckedStateChangeListener { _, ints ->
            val type = when (ints.firstOrNull()) {
                CommonId.type_wish -> InterestCollectType.TYPE_WISH
                CommonId.type_collect -> InterestCollectType.TYPE_COLLECT
                CommonId.type_do -> InterestCollectType.TYPE_DO
                CommonId.type_on_hold -> InterestCollectType.TYPE_ON_HOLD
                CommonId.type_dropped -> InterestCollectType.TYPE_DROPPED
                else -> null
            }

            if (type != null) {
                viewModel.listType = type
                viewModel.selectTag = null
                viewModel.refresh()
            }
        }

        // 类别切换
        filter.chipMediaType.setOnFastLimitClickListener {
            FilterOptionDialog.show(
                fragmentManager = childFragmentManager,
                options = viewModel.createFilterOptions(),
                onSelectedChangeListener = {
                    refreshSortAndType(it)
                }
            )
        }
    }

    override fun initListener() {
        super.initListener()

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_save) {
            RouteHelper.jumpMediaDetail(it.id)
        }
    }

    override fun autoInitData() {
        // 嵌套在 Profile 页面的情况
        if (viewModel.requireLogin) {
            UserHelper.observeUserInfo(this) {
                viewModel.userId = it.id.orEmpty()
                viewModel.refresh()
            }
        } else {
            super.autoInitData()
        }
    }

    /**
     * 筛选结果
     */
    @SuppressLint("WrongConstant")
    private fun refreshSortAndType(items: List<FilterEntity.OptionItem>) {
        // 排序
        val orderByItem = items.find { item -> item.field == viewModel.filterFieldOrderBy }
        if (orderByItem != null) {
            viewModel.sortType = orderByItem.id
        }

        // TAG
        val tagItem = items.find { item -> item.field == viewModel.filterFieldTag }
        if (tagItem != null) {
            viewModel.selectTag = tagItem.id
        } else {
            viewModel.selectTag = null
        }

        // 媒体类型
        val mediaTypeItem = items.find { item -> item.field == viewModel.filterFieldMediaType }
        if (mediaTypeItem != null) {
            val mediaType = mediaTypeItem.id
            filter.chipMediaType.text = GlobalConfig.mediaTypeName(mediaType)

            // 设置文案
            filter.typeWish.text =
                InterestType.string(InterestType.TYPE_WISH, mediaType)
            filter.typeCollect.text =
                InterestType.string(InterestType.TYPE_COLLECT, mediaType)
            filter.typeDo.text = InterestType.string(InterestType.TYPE_DO, mediaType)
            filter.typeOnHold.text =
                InterestType.string(InterestType.TYPE_ON_HOLD, mediaType)
            filter.typeDropped.text =
                InterestType.string(InterestType.TYPE_DROPPED, mediaType)

            viewModel.mediaType = mediaType

            // 切换了媒体类型清空 TAG
            if (mediaType != viewModel.mediaType) {
                viewModel.selectTag = null
            }
        }

        // 刷新
        viewModel.refresh()
    }

    companion object {
        fun newInstance(userId: String, requireLogin: Boolean = false): SaveListFragment {
            return SaveListFragment().apply {
                arguments = bundleOf(
                    NavKey.KEY_STRING to userId,
                    NavKey.KEY_BOOLEAN to requireLogin
                )
            }
        }
    }
}