package com.xiaoyv.bangumi.ui.feature.person.picture

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.blankj.utilcode.util.DebouncingUtils
import com.chad.library.adapter.base.BaseDifferAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListFragment
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.response.GalleryEntity
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.dialog.UiDialog

/**
 * Class: [PersonPictureFragment]
 *
 * @author why
 * @since 1/13/24
 */
class PersonPictureFragment : BaseListFragment<GalleryEntity, PersonPictureViewModel>() {
    override val isOnlyOnePage: Boolean
        get() = false
    override val loadingBias: Float
        get() = 0.3f

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.personId = arguments.getString(NavKey.KEY_STRING).orEmpty()
    }

    override fun initData() {
        super.initData()
        contentAdapter.setItemAnimation(BaseQuickAdapter.AnimationType.ScaleIn)

        // 移除滑动停止加载图片
        binding.rvContent.removeImageScrollLoadController()
    }

    override fun initListener() {
        super.initListener()

        // 单击预览
        contentAdapter.addOnItemChildClickListener(R.id.iv_image) { adapter, v, position ->
            val item = adapter.getItem(position) ?: return@addOnItemChildClickListener
            if (DebouncingUtils.isValid(v)) {
                RouteHelper.jumpPreviewImage(item.largeImageUrl)
            }
        }
    }

    override fun onCreateContentAdapter(): BaseDifferAdapter<GalleryEntity, *> {
        return PersonPictureAdapter()
    }

    override fun onCreateLayoutManager(): LayoutManager {
        return StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    override fun createLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(requireContext())
    }

    companion object {
        fun newInstance(personId: String): PersonPictureFragment {
            return PersonPictureFragment().apply {
                arguments = bundleOf(NavKey.KEY_STRING to personId)
            }
        }
    }
}