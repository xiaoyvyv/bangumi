package com.xiaoyv.bangumi.special.subtitle

import android.content.Intent
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.CallSuper
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.UriUtils
import com.xiaoyv.bangumi.databinding.ActivitySubtitleToolBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.common.config.annotation.SubtitleActionType
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.i18n
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.showOptionsDialog
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.subtitle.media.entity.FFProbeEntity
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.dialog.UiDialog

class SubtitleToolActivity :
    BaseViewModelActivity<ActivitySubtitleToolBinding, SubtitleToolViewModel>() {

    private val selectSubtitleLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            viewModel.loadMediaStreamInfo(it ?: return@registerForActivityResult)
        }

    override fun initView() {
        binding.toolbar.initNavBack(this)
    }

    override fun initData() {

    }

    override fun initListener() {
        binding.btnSubtitle.setOnFastLimitClickListener {
            selectSubtitleLauncher.launch("*/*")
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.onStreamInfoLiveData.observe(this) {
            val streams = it.orEmpty()
            if (streams.isNotEmpty()) {
                showOptionsDialog(
                    title = i18n(CommonString.subtitle_count, streams.size),
                    items = streams.map { stream -> stream.displayTitle },
                    onItemClick = { item, index ->
                        showSubtitleActionOptions(item, streams[index])
                    }
                )
            }
        }

        viewModel.onSubtitleLiveData.observe(this) {
            val result = it ?: return@observe
            when (result.actionType) {
                // 复制内容
                SubtitleActionType.TYPE_COPY -> {
                    ClipboardUtils.copyText(result.file.readText())
                    showToast(i18n(CommonString.common_copy_success))
                }
                // 分享导出
                SubtitleActionType.TYPE_SHARE -> {
                    var intent = Intent(Intent.ACTION_SEND)
                    intent.setType("text/*")
                    intent.putExtra(Intent.EXTRA_STREAM, UriUtils.file2Uri(result.file))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent = Intent.createChooser(intent, i18n(CommonString.subtitle_export))
                    ActivityUtils.startActivity(intent)
                }
                // 翻译字幕
                SubtitleActionType.TYPE_TRANSLATE -> {
                    viewModel.translateSubtitle(result)
                }
            }
        }

        // 翻译进度
        viewModel.onTranslateProgress.observe(this) {
            debugLog {
                i18n(
                    CommonString.subtitle_translate_progress,
                    it.first,
                    it.second,
                    it.first * 100f / it.second.toFloat()
                )
            }
        }
    }

    /**
     * 操作选项
     */
    private fun showSubtitleActionOptions(item: String, stream: FFProbeEntity.Stream) {
        val actions = mapOf(
            SubtitleActionType.TYPE_SHARE to i18n(CommonString.subtitle_dialog_menu_export),
            SubtitleActionType.TYPE_COPY to i18n(CommonString.subtitle_dialog_menu_copy),
            SubtitleActionType.TYPE_TRANSLATE to i18n(CommonString.subtitle_dialog_menu_translate),
        )

        showOptionsDialog(
            title = i18n(CommonString.subtitle_dialog_title, item),
            items = actions.values.toList(),
            onItemClick = { _, index ->
                viewModel.extractSubtitle(actions.keys.toList()[index], stream)
            }
        )
    }

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }

    @CallSuper
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }

}