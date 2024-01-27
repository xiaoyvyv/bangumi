package com.xiaoyv.bangumi.special.syncer.list

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.xiaoyv.bangumi.databinding.ActivitySyncerListDialogBinding
import com.xiaoyv.common.helper.ConfigHelper

/**
 * Class: [SyncerListDialog]
 *
 * @author why
 * @since 1/27/24
 */
class SyncerListDialog : BottomSheetDialogFragment() {

    private val parentActivity: SyncerListActivity
        get() = requireActivity() as SyncerListActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = ActivitySyncerListDialogBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = ActivitySyncerListDialogBinding.bind(view)

        parentActivity.viewModel.onSyncProgress.observe(viewLifecycleOwner) {
            binding.pbProgress.max = it.second
            binding.pbProgress.setProgress(it.first, true)

            binding.tvFinish.text = String.format("已同步：%s", it.first)
            binding.tvTotal.text = String.format("全部：%s", it.second)
        }

        parentActivity.viewModel.onSyncSubject.observe(viewLifecycleOwner) {
            binding.tvDesc.text = String.format("正在同步：%s", it)
        }

        parentActivity.viewModel.onSyncFinish.observe(viewLifecycleOwner) {
            isCancelable = true
            dismissAllowingStateLoss()
        }
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window ?: return
        window.setBackgroundDrawableResource(R.color.transparent)
        window.setDimAmount(ConfigHelper.DIALOG_DIM_AMOUNT)
    }
}