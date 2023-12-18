package com.xiaoyv.bangumi.ui.media.action

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SnackbarUtils
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentMediaActionEpBinding
import com.xiaoyv.bangumi.ui.media.detail.overview.binder.OverviewEpBinder
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.hideSnackBar
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.kts.showSnackBar
import com.xiaoyv.widget.kts.dpi
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.getParcelObjList
import com.xiaoyv.widget.kts.updateWindowParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

/**
 * Class: [MediaEpActionDialog]
 *
 * @author why
 * @since 12/18/23
 */
class MediaEpActionDialog : DialogFragment() {
    private var onSaveListener: ((Int) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentMediaActionEpBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val mediaId = arguments?.getString(NavKey.KEY_STRING).orEmpty()
        val mediaName = arguments?.getString(NavKey.KEY_STRING_SECOND).orEmpty()
        val myProgress = arguments?.getInt(NavKey.KEY_INTEGER) ?: 0
        val progresses =
            arguments?.getParcelObjList<MediaDetailEntity.MediaProgress>(NavKey.KEY_PARCELABLE_LIST)
                .orEmpty()

        val binding = FragmentMediaActionEpBinding.bind(view)
        initView(binding, mediaId, mediaName, progresses, myProgress)
    }

    private fun initView(
        binding: FragmentMediaActionEpBinding,
        mediaId: String,
        mediaName: String,
        progresses: List<MediaDetailEntity.MediaProgress>,
        myProgress: Int,
    ) {
        binding.tvTitle.text = mediaName
        binding.ivCancel.setOnClickListener {
            dismissAllowingStateLoss()
        }

        // 格子填充
        val epAdapter = OverviewEpBinder.ItemEpAdapter(
            selectedMode = true,
            selectIndex = myProgress - 1
        )
        binding.rvEp.updateLayoutParams<ConstraintLayout.LayoutParams> {
            matchConstraintMaxHeight = (ScreenUtils.getAppScreenHeight() * 0.7f).roundToInt()
        }
        binding.rvEp.hasFixedSize()
        binding.rvEp.adapter = epAdapter

        // 点击更新颜色
        epAdapter.setOnDebouncedChildClickListener(R.id.item_ep) {
            epAdapter.selectIndex = epAdapter.itemIndexOfFirst(it)
            if (epAdapter.selectIndex >= 0 && epAdapter.selectIndex < epAdapter.itemCount) {
                epAdapter.notifyItemRangeChanged(
                    0,
                    epAdapter.itemCount,
                    OverviewEpBinder.ItemEpAdapter.PAYLOAD_REFRESH_COLOR
                )

                finishEp(mediaId, it.number)
            }

            dismissAllowingStateLoss()
        }

        launchUI {
            epAdapter.submitList(withContext(Dispatchers.IO) {
                progresses.filterNot { it.isNotEp }
            })
        }
    }

    /**
     * 保存进度
     */
    private fun finishEp(mediaId: String, number: String) {
        launchUI(
            error = {
                it.printStackTrace()

                showSnackBar(it.errorMsg, error = true)
            },
            block = {
                require(mediaId.isNotBlank()) { "条目信息丢失" }
                require(number.isNotBlank()) { "章节信息丢失" }

                showSnackBar("正在保存进度...", SnackbarUtils.LENGTH_INDEFINITE)

                withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.updateMediaProgress(mediaId, watch = number)
                }

                UserHelper.notifyActionChange(BgmPathType.TYPE_EP)
                hideSnackBar()

                onSaveListener?.invoke(number.toIntOrNull() ?: 0)
            }
        )
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog ?: return
        val window = dialog.window ?: return

        window.setBackgroundDrawableResource(android.R.color.transparent)
        window.setDimAmount(0.25f)
        window.updateWindowParams {
            width = ScreenUtils.getScreenWidth() - 32.dpi
            gravity = Gravity.CENTER
        }
    }

    companion object {
        fun show(
            fragmentManager: FragmentManager,
            mediaId: String,
            mediaName: String,
            totalEp: ArrayList<MediaDetailEntity.MediaProgress>?,
            myProgress: Int = 0,
            onSaveListener: ((Int) -> Unit)? = null,
        ) {
            MediaEpActionDialog().apply {
                this.onSaveListener = onSaveListener
                this.arguments = bundleOf(
                    NavKey.KEY_STRING to mediaId,
                    NavKey.KEY_STRING_SECOND to mediaName,
                    NavKey.KEY_PARCELABLE_LIST to totalEp,
                    NavKey.KEY_INTEGER to myProgress
                )
            }.show(fragmentManager, "MediaEpActionDialog")
        }
    }
}