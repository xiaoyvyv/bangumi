package com.xiaoyv.bangumi.ui.profile.page.index

import android.os.Bundle
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListFragment
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.bangumi.ui.discover.index.list.IndexListAdapter
import com.xiaoyv.bangumi.ui.discover.index.list.IndexListViewModel
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.IndexItemEntity
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.databinding.ViewIndexFilterBinding
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.CommonId
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [UserIndexFragment]
 *
 * @author why
 * @since 12/14/23
 */
class UserIndexFragment : BaseListFragment<IndexItemEntity, IndexListViewModel>() {
    private lateinit var filter: ViewIndexFilterBinding

    override val isOnlyOnePage: Boolean
        get() = false

    override val loadingBias: Float
        get() = 0.3f

    private var onSelectedListener: ((IndexItemEntity) -> Unit)? = null

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.userId = arguments.getString(NavKey.KEY_STRING).orEmpty()
        viewModel.selectedMode = arguments.getBoolean(NavKey.KEY_BOOLEAN, false)
        viewModel.requireLogin = arguments.getBoolean(NavKey.KEY_BOOLEAN_SECOND, false)

        // 个人中心嵌套，默认显示自己的目录
        if (viewModel.requireLogin) {
            viewModel.isSortByNewest = true
        }
    }

    override fun initView() {
        super.initView()
        if (viewModel.requireLogin && !viewModel.selectedMode) {
            filter = ViewIndexFilterBinding.inflate(layoutInflater, binding.flContainer, true)
            filter.root.doOnPreDraw {
                binding.rvContent.updateLayoutParams<MarginLayoutParams> {
                    topMargin = filter.root.height
                }
            }

            // 创建的和收藏的切换
            filter.listType.setOnCheckedStateChangeListener { _, ints ->
                when (ints.firstOrNull()) {
                    CommonId.type_create -> {
                        viewModel.isSortByNewest = true
                        viewModel.refresh()
                    }

                    CommonId.type_collect -> {
                        viewModel.isSortByNewest = false
                        viewModel.refresh()
                    }
                }
            }
        }
    }

    override fun initListener() {
        super.initListener()

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_index) {
            if (viewModel.selectedMode) {
                onSelectedListener?.invoke(it)
            } else {
                RouteHelper.jumpIndexDetail(it.id)
            }
        }
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<IndexItemEntity, *> {
        return IndexListAdapter()
    }

    override fun LifecycleOwner.initViewObserverExt() {
        // 自己的内容删除时刷新列表
        if (viewModel.isMine) {
            UserHelper.observeAction(this) {
                if (it == BgmPathType.TYPE_INDEX) {
                    viewModel.refresh()
                }
            }
        }
    }

    override fun autoInitData() {
        // 嵌套在 Profile 页面的情况
        if (viewModel.requireLogin) {
            UserHelper.observeUserInfo(this) {
                viewModel.userId = it.id
                viewModel.refresh()
            }
        } else {
            super.autoInitData()
        }
    }

    override fun onListDataFinish(list: List<IndexItemEntity>) {

    }

    companion object {
        fun newInstance(
            userId: String,
            selectedMode: Boolean = false,
            onSelectedListener: ((IndexItemEntity) -> Unit)? = null,
            requireLogin: Boolean,
        ): UserIndexFragment {
            return UserIndexFragment().apply {
                this.onSelectedListener = onSelectedListener
                this.arguments = bundleOf(
                    NavKey.KEY_STRING to userId,
                    NavKey.KEY_BOOLEAN to selectedMode,
                    NavKey.KEY_BOOLEAN_SECOND to requireLogin
                )
            }
        }
    }
}