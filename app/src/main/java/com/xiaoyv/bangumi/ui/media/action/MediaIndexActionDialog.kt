package com.xiaoyv.bangumi.ui.media.action

import android.R
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SnackbarUtils
import com.xiaoyv.bangumi.databinding.FragmentMediaActionIndexBinding
import com.xiaoyv.bangumi.ui.profile.page.index.UserIndexFragment
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.LazyUtils.loadRootFragment
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.IndexItemEntity
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.IndexAttachCatType
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.hideSnackBar
import com.xiaoyv.common.kts.onStartConfig
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.kts.showInputLine2Dialog
import com.xiaoyv.common.kts.showSnackBar
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.kts.dpi
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.updateWindowParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

/**
 * Class: [MediaIndexActionDialog]
 *
 * @author why
 * @since 12/3/23
 */
class MediaIndexActionDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentMediaActionIndexBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentMediaActionIndexBinding.bind(view)
        val mediaId = arguments?.getString(NavKey.KEY_STRING).orEmpty()
        val mediaName = arguments?.getString(NavKey.KEY_STRING_SECOND).orEmpty()

        // 显示我的目录列表
        loadRootFragment(
            binding.flContainer.id,
            UserIndexFragment.newInstance(
                userId = UserHelper.currentUser.id.orEmpty(),
                selectedMode = true,
                onSelectedListener = {
                    requireActivity().showConfirmDialog(
                        message = String.format("是否添加 %s 到目录 %s", mediaName, it.title),
                        onConfirmClick = {
                            saveMediaToIndex(mediaId, it)
                        }
                    )
                },
                requireLogin = true
            )
        )

        initView(binding, mediaName)
    }

    private fun initView(binding: FragmentMediaActionIndexBinding, mediaName: String) {
        binding.tvTitle.text = mediaName
        binding.ivCancel.setOnClickListener {
            dismissAllowingStateLoss()
        }

        binding.tvNew.setOnFastLimitClickListener {
            requireActivity().showInputLine2Dialog(
                title = "创建一个新目录",
                inputHint1 = "目录标题",
                inputHint2 = "目录介绍",
                onInput = { title, content ->
                    createIndex(title, content)
                }
            )
        }
    }

    /**
     * 将媒体关联到目标目录
     */
    private fun saveMediaToIndex(mediaId: String, index: IndexItemEntity) {
        launchUI(
            error = {
                it.printStackTrace()

                showSnackBar(it.errorMsg, error = true)
            },
            block = {
                require(mediaId.isNotBlank()) { "条目信息丢失" }
                require(index.id.isNotBlank()) { "目录信息丢失" }

                showSnackBar("正在保存到目录...", SnackbarUtils.LENGTH_INDEFINITE)

                withContext(Dispatchers.IO) {
                    // 添加媒体条目到章节
                    BgmApiManager.bgmWebApi.addMediaToIndex(
                        indexId = index.id,
                        formHash = UserHelper.formHash,
                        cat = IndexAttachCatType.TYPE_SUBJECT,
                        targetId = mediaId,
                    )
                }

                UserHelper.notifyActionChange(BgmPathType.TYPE_INDEX)

                hideSnackBar()
            }
        )
    }

    /**
     * 创建一个目录
     */
    private fun createIndex(title: String, content: String) {
        launchUI(
            error = {
                it.printStackTrace()

                showSnackBar(it.errorMsg, error = true)
            },
            block = {
                require(title.isNotBlank()) { "请输入目录标题" }
                require(content.isNotBlank()) { "请输入目录介绍" }

                showSnackBar("正在创建目录...", SnackbarUtils.LENGTH_INDEFINITE)

                withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.createIndex(
                        formHash = UserHelper.formHash,
                        title = title,
                        desc = content
                    )
                }

                UserHelper.notifyActionChange(BgmPathType.TYPE_INDEX)

                hideSnackBar()
            }
        )
    }

    override fun onStart() {
        super.onStart()
        onStartConfig(fixHeight = true)
    }

    companion object {
        fun show(fragmentManager: FragmentManager, mediaId: String, mediaName: String) {
            MediaIndexActionDialog()
                .apply {
                    arguments = bundleOf(
                        NavKey.KEY_STRING to mediaId,
                        NavKey.KEY_STRING_SECOND to mediaName
                    )
                }.show(fragmentManager, "MediaIndexActionDialog")
        }
    }
}