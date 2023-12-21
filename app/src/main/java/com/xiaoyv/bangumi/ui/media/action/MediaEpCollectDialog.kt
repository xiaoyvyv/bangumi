package com.xiaoyv.bangumi.ui.media.action

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SnackbarUtils
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentMediaActionEpCollectBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.MediaChapterEntity
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.EpCollectType
import com.xiaoyv.common.config.annotation.InterestType
import com.xiaoyv.common.config.annotation.TopicType
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.hideSnackBar
import com.xiaoyv.common.kts.showSnackBar
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.kts.dpi
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.getParcelObj
import com.xiaoyv.widget.kts.updateWindowParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [MediaEpCollectDialog]
 *
 * @author why
 * @since 12/18/23
 */
class MediaEpCollectDialog : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentMediaActionEpCollectBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val entity = arguments?.getParcelObj<MediaChapterEntity>(NavKey.KEY_PARCELABLE) ?: return
        initView(FragmentMediaActionEpCollectBinding.bind(view), entity)
    }

    private fun initView(binding: FragmentMediaActionEpCollectBinding, entity: MediaChapterEntity) {
        binding.tvTitle.text = entity.titleNative.ifBlank { entity.titleCn }
        binding.ivCancel.setOnClickListener {
            dismissAllowingStateLoss()
        }

        // 设置状态
        when (entity.collectType) {
            InterestType.TYPE_WISH -> binding.gpButtons.check(R.id.btn_wish)
            InterestType.TYPE_COLLECT -> binding.gpButtons.check(R.id.btn_collect)
            InterestType.TYPE_DROPPED -> binding.gpButtons.check(R.id.btn_dropped)
        }

        binding.tvTitleCn.isVisible = entity.titleCn.isNotBlank()
        binding.tvTitleCn.text = buildString {
            append("中文名：")
            append(entity.titleCn)
        }
        binding.tvDesc.text = entity.time
        binding.tvComment.text = String.format("讨论：%d，点击查看", entity.commentCount)

        binding.tvComment.setOnFastLimitClickListener {
            jumpDetail(entity)
        }

        binding.tvDesc.setOnFastLimitClickListener {
            jumpDetail(entity)
        }

        binding.tvTitleCn.setOnFastLimitClickListener {
            jumpDetail(entity)
        }

        binding.gpButtons.addOnButtonCheckedListener { _, i, checked ->
            if (!checked) {
                return@addOnButtonCheckedListener
            }

            val epCollectType = when (i) {
                R.id.btn_wish -> {
                    entity.collectType = InterestType.TYPE_WISH
                    EpCollectType.TYPE_QUEUE
                }

                R.id.btn_collect -> {
                    entity.collectType = InterestType.TYPE_COLLECT
                    EpCollectType.TYPE_WATCHED
                }

                R.id.btn_dropped -> {
                    entity.collectType = InterestType.TYPE_DROPPED
                    EpCollectType.TYPE_DROP
                }

                else -> {
                    entity.collectType = InterestType.TYPE_UNKNOWN
                    EpCollectType.TYPE_REMOVE
                }
            }

            saveEpCollectStatus(entity, epCollectType)
        }
    }

    /**
     * 刷新章节进度
     */
    private fun saveEpCollectStatus(
        entity: MediaChapterEntity,
        @EpCollectType epCollectType: String,
    ) {
        launchUI(
            error = {
                it.printStackTrace()

                showSnackBar(it.errorMsg, error = true)
            },
            block = {
                showSnackBar("正在为你保存进度...", SnackbarUtils.LENGTH_INDEFINITE)
                withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.postEpCollect(
                        epId = entity.id,
                        epCollectType = epCollectType,
                        gh = UserHelper.formHash
                    )
                }
                hideSnackBar()

                UserHelper.notifyActionChange(BgmPathType.TYPE_EP)

                dismissAllowingStateLoss()
            }
        )
    }

    private fun jumpDetail(entity: MediaChapterEntity) {
        dismissAllowingStateLoss()
        RouteHelper.jumpTopicDetail(entity.id, TopicType.TYPE_EP)
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

        fun show(fragmentManager: FragmentManager, chapterEntity: MediaChapterEntity) {
            MediaEpCollectDialog().apply {
                arguments = bundleOf(NavKey.KEY_PARCELABLE to chapterEntity)
            }.show(fragmentManager, "MediaEpCollectDialog")
        }
    }
}