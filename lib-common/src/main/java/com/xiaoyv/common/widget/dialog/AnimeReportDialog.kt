package com.xiaoyv.common.widget.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.ActivityUtils
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.R
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.ReportEntity
import com.xiaoyv.common.api.parser.impl.parserReportForm
import com.xiaoyv.common.config.annotation.ReportType
import com.xiaoyv.common.databinding.ViewReportBinding
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.onStartConfig
import com.xiaoyv.common.kts.i18n
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [AnimeReportDialog]
 *
 * @author why
 * @since 12/11/23
 */
class AnimeReportDialog : DialogFragment() {
    private var reportForms: ReportEntity? = null
    private var reportType: String = ReportType.TYPE_1
    private var userId = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ViewReportBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = ViewReportBinding.bind(view)
        userId = arguments?.getString(NavKey.KEY_STRING).orEmpty()
        reportType = arguments?.getString(NavKey.KEY_STRING_SECOND).orEmpty()

        initView(binding)
        initListener(binding)
    }

    private fun initView(binding: ViewReportBinding) {
        launchUI(
            block = {
                reportForms = withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.queryReportForm(userId, reportType).parserReportForm()
                }
                binding.submitBtn.isEnabled = true
            }
        )
    }

    private fun initListener(binding: ViewReportBinding) {
        binding.submitBtn.setOnFastLimitClickListener {
            val value = when (binding.collectTypeRadioGroup.checkedChipId) {
                R.id.radio_ad -> "8"
                R.id.radio_argument -> "7"
                R.id.radio_attack -> "1"
                R.id.radio_irrelevant -> "2"
                R.id.radio_political -> "3"
                R.id.radio_illegal -> "4"
                R.id.radio_privacy -> "5"
                R.id.radio_score -> "6"
                R.id.radio_spoiler -> "9"
                R.id.radio_other -> "99"
                else -> ""
            }

            if (value.isBlank()) {
                toast(i18n(CommonString.action_report_select_one))
                return@setOnFastLimitClickListener
            }
            report(binding, value)
        }
    }

    private fun report(binding: ViewReportBinding, value: String) {
        launchUI(
            error = {
                it.printStackTrace()
                hideLoading(binding)
                toast(it.errorMsg)
            },
            block = {
                showLoading(binding)
                withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.postReport(
                        binding.comment.text.toString().trim(),
                        value,
                        reportForms?.hiddenForm.orEmpty()
                    )
                }
                hideLoading(binding)
                toast(i18n(CommonString.action_report_success))

                dismissAllowingStateLoss()
            }
        )
    }

    private fun showLoading(binding: ViewReportBinding) {
        binding.stateView.showLoading()
    }

    private fun hideLoading(binding: ViewReportBinding) {
        binding.stateView.showContent()
    }

    override fun onStart() {
        super.onStart()
        onStartConfig()
    }

    companion object {

        /**
         * 注意，需要 Int 类型的 UID
         */
        fun show(userNumberId: String, @ReportType type: String) {
            val activity = ActivityUtils.getTopActivity() as? FragmentActivity ?: return

            AnimeReportDialog().apply {
                arguments = bundleOf(
                    NavKey.KEY_STRING to userNumberId,
                    NavKey.KEY_STRING_SECOND to type
                )
            }.show(activity.supportFragmentManager, "AnimeReportDialog")
        }
    }
}