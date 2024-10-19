package com.xiaoyv.bangumi.special.syncer.list

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.xiaoyv.bangumi.databinding.ActivitySyncerListDialogBinding
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.i18n

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

            binding.tvFinish.text = i18n(CommonString.syncer_progress_finish, it.first)
            binding.tvTotal.text = i18n(CommonString.syncer_progress_total, it.second)
        }

        parentActivity.viewModel.onSyncSubject.observe(viewLifecycleOwner) {
            binding.tvDesc.text = i18n(CommonString.syncer_progress_current, it)
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