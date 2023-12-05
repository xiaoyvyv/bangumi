package com.xiaoyv.bangumi.ui.feature.person.character

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.layoutmanager.QuickGridLayoutManager
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListFragment
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.CharacterEntity
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.dpi

/**
 * Class: [PersonCharacterFragment]
 *
 * @author why
 * @since 12/4/23
 */
class PersonCharacterFragment : BaseListFragment<CharacterEntity, PersonCharacterViewModel>() {

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.personId = arguments.getString(NavKey.KEY_STRING).orEmpty()
        viewModel.isVirtual = arguments.getBoolean(NavKey.KEY_BOOLEAN)
    }

    override val isOnlyOnePage: Boolean
        get() = true

    override fun initView() {
        super.initView()
        binding.rvContent.updatePadding(8.dpi, 8.dpi, 8.dpi, 8.dpi)
    }

    override fun initListener() {
        super.initListener()

        contentAdapter.setOnDebouncedChildClickListener(R.id.iv_cover) {
            RouteHelper.jumpPerson(it.id, true)
        }
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<CharacterEntity, *> {
        return PersonCharacterAdapter()
    }

    override fun onCreateLayoutManager(): LinearLayoutManager {
        return QuickGridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false)
    }

    companion object {
        fun newInstance(personId: String, isVirtual: Boolean): PersonCharacterFragment {
            return PersonCharacterFragment().apply {
                arguments = bundleOf(
                    NavKey.KEY_STRING to personId,
                    NavKey.KEY_BOOLEAN to isVirtual
                )
            }
        }
    }
}