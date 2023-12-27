package com.xiaoyv.common.widget.dialog.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.config.bean.FilterEntity
import com.xiaoyv.common.databinding.ViewFilterDialogBinding
import com.xiaoyv.common.kts.onStartConfig
import com.xiaoyv.widget.kts.getParcelObjList

/**
 * Class: [FilterOptionDialog]
 *
 * @author why
 * @since 12/27/23
 */
class FilterOptionDialog : DialogFragment() {
    var onSelectedChange: (options: List<FilterEntity.OptionItem>) -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = ViewFilterDialogBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val options = arguments?.getParcelObjList<FilterEntity>(NavKey.KEY_PARCELABLE_LIST)

        val binding = ViewFilterDialogBinding.bind(view)
        initView(binding, options)
    }

    private fun initView(binding: ViewFilterDialogBinding, options: List<FilterEntity>?) {
        val optionAdapter = FilterHeaderAdapter(options.orEmpty(), false, flex = true)

        binding.rvOptions.itemAnimator = null
        binding.rvOptions.adapter = optionAdapter

        optionAdapter.onSelectedChangeListener = {
            onSelectedChange(it)
            dismissAllowingStateLoss()
        }
    }

    override fun onStart() {
        super.onStart()
        onStartConfig()
    }

    companion object {
        fun show(
            fragmentManager: FragmentManager,
            options: List<FilterEntity>,
            onSelectedChangeListener: (options: List<FilterEntity.OptionItem>) -> Unit = {},
        ) {
            FilterOptionDialog()
                .apply {
                    onSelectedChange = onSelectedChangeListener
                    arguments = bundleOf(NavKey.KEY_PARCELABLE_LIST to ArrayList(options))
                }
                .show(fragmentManager, "MediaOptionDialog")
        }
    }
}